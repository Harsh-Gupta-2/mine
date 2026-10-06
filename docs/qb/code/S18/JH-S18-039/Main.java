import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Main {
    record Row(String id,String value) {}
    record Report(List<String> accepted,List<String> rejected) {}
    static Report partition(List<Row> rows,Function<Row,String> error){List<String> accepted=new ArrayList<>(),rejected=new ArrayList<>();for(Row row:rows){String issue=error.apply(row);if(issue==null)accepted.add(row.id());else rejected.add(row.id()+":"+issue);}return new Report(List.copyOf(accepted),List.copyOf(rejected));}
    public static void main(String[] args){Report report=partition(List.of(new Row("1","ok"),new Row("2",""),new Row("3","good")),row->row.value().isBlank()?"empty":null);
        if(!report.accepted().equals(List.of("1","3"))||!report.rejected().equals(List.of("2:empty")))throw new AssertionError(report);
        if(!partition(List.of(),row->null).accepted().isEmpty())throw new AssertionError("empty input");
        System.out.println("Accepted "+report.accepted().size()+", rejected "+report.rejected().size()+": "+report.rejected());}
}