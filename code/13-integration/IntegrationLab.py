import hashlib
import io
import json
import unittest
from dataclasses import dataclass
from decimal import Decimal, InvalidOperation
from pathlib import Path


@dataclass(frozen=True)
class SourceRow:
    identity: str
    amount: str


def map_row(row):
    if not row.identity:
        raise ValueError("missing source identity")
    try:
        amount = Decimal(row.amount)
        if not amount.is_finite() or amount < 0 or amount > Decimal("999999999.99"):
            raise ValueError("amount outside mapping contract")
        rounded = amount.quantize(Decimal("0.01"))
        if rounded != amount:
            raise ValueError("excess fractional precision")
        return {"source_id": row.identity, "minor_units": int(rounded * 100)}
    except InvalidOperation as error:
        raise ValueError("invalid decimal amount") from error


class ImportJob:
    def __init__(self, tenant, rows, mapping_version):
        self.tenant = tenant
        self.rows = tuple(rows)
        self.mapping_version = mapping_version
        encoded = json.dumps([(row.identity, row.amount) for row in self.rows]).encode()
        self.fingerprint = hashlib.sha256(encoded).hexdigest()
        self.state = {"checkpoint": 0, "accepted": {}, "quarantine": {}}

    def resume(self, fingerprint, mapping_version):
        if (fingerprint, mapping_version) != (self.fingerprint, self.mapping_version):
            raise ValueError("restart input or mapping changed")
        return self.state["checkpoint"]

    def chunk(self, size, fail_before_commit=False):
        if size <= 0:
            raise ValueError("positive chunk size required")
        start = self.state["checkpoint"]
        end = min(start + size, len(self.rows))
        accepted = dict(self.state["accepted"])
        quarantine = dict(self.state["quarantine"])
        for position in range(start, end):
            row = self.rows[position]
            key = (self.tenant, self.fingerprint, self.mapping_version, position)
            try:
                accepted[key] = map_row(row)
            except ValueError as error:
                quarantine[key] = {"source_position": position, "reason": str(error)}
        if fail_before_commit:
            raise RuntimeError("arranged failure before local state commit")
        self.state = {"checkpoint": end, "accepted": accepted, "quarantine": quarantine}


class IntegrationTests(unittest.TestCase):
    def test_decimal_contract(self):
        self.assertEqual(map_row(SourceRow("row-1", "12.30"))["minor_units"], 1230)
        for value in ("NaN", "Infinity", "-1", "1.001", "not-a-number"):
            with self.assertRaises(ValueError):
                map_row(SourceRow("row-1", value))

    def test_chunk_rollback_and_resume(self):
        job = ImportJob("tenant-a", [SourceRow("one", "1.00"), SourceRow("two", "2.00")], "v1")
        with self.assertRaises(RuntimeError):
            job.chunk(2, fail_before_commit=True)
        self.assertEqual(job.state, {"checkpoint": 0, "accepted": {}, "quarantine": {}})
        job.chunk(1)
        self.assertEqual(job.resume(job.fingerprint, "v1"), 1)
        job.chunk(1)
        job.chunk(1)
        self.assertEqual(len(job.state["accepted"]), 2)

    def test_quarantine_and_checkpoint_agree(self):
        job = ImportJob("tenant-a", [SourceRow("ok", "2"), SourceRow("bad", "2.001")], "v1")
        job.chunk(2)
        self.assertEqual(job.state["checkpoint"], 2)
        self.assertEqual(len(job.state["accepted"]), 1)
        self.assertEqual(len(job.state["quarantine"]), 1)
        self.assertNotIn("2.001", json.dumps(list(job.state["quarantine"].values())))

    def test_restart_contract_and_tenant_scope(self):
        rows = [SourceRow("one", "1")]
        first = ImportJob("tenant-a", rows, "v1")
        second = ImportJob("tenant-b", rows, "v1")
        with self.assertRaises(ValueError):
            first.resume("different-input", "v1")
        with self.assertRaises(ValueError):
            first.resume(first.fingerprint, "v2")
        first.chunk(1)
        second.chunk(1)
        self.assertNotEqual(set(first.state["accepted"]), set(second.state["accepted"]))


if __name__ == "__main__":
    capture = io.StringIO()
    suite = unittest.defaultTestLoader.loadTestsFromTestCase(IntegrationTests)
    result = unittest.TextTestRunner(stream=capture, verbosity=2).run(suite)
    report = {"status": "PASS" if result.wasSuccessful() else "FAIL", "tests": result.testsRun,
              "scope": "In-memory mapping/checkpoint model; no source, target or iPaaS integration",
              "output": capture.getvalue()}
    Path(__file__).with_name("execution.json").write_text(json.dumps(report, indent=2), encoding="utf-8")
    print(json.dumps(report, indent=2))
    raise SystemExit(0 if result.wasSuccessful() else 1)