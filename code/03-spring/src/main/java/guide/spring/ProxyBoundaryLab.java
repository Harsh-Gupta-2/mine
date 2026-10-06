package guide.spring;

import java.util.ArrayList;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;

public final class ProxyBoundaryLab {
    public interface Operations {
        void outer();
        void inner();
    }

    public static final class Target implements Operations {
        private final List<String> events;

        Target(List<String> events) {
            this.events = events;
        }

        public void outer() {
            events.add("target:outer");
            inner();
        }

        public void inner() {
            events.add("target:inner");
        }
    }

    public static void main(String[] args) {
        var events = new ArrayList<String>();
        var factory = new ProxyFactory(new Target(events));
        factory.setInterfaces(Operations.class);
        factory.addAdvice((MethodInterceptor) invocation -> {
            events.add("advice:" + invocation.getMethod().getName());
            return invocation.proceed();
        });
        Operations proxy = (Operations) factory.getProxy();
        proxy.outer();
        if (!events.equals(List.of("advice:outer", "target:outer", "target:inner"))) {
            throw new AssertionError("Self-invocation must bypass this proxy's advice: " + events);
        }
        events.clear();
        proxy.inner();
        if (!events.equals(List.of("advice:inner", "target:inner"))) {
            throw new AssertionError("External invocation must cross the proxy: " + events);
        }
        System.out.println("ProxyBoundaryLab: all checks passed");
    }
}