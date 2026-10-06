import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Main {
    record Validation<T>(T value,List<String> errors) { boolean valid(){return errors.isEmpty();} }
    static <T> Validation<T> validate(T input,List<Function<T,String>> checks){List<String> errors=new ArrayList<>();for(Function<T,String> check:checks){String error=check.apply(input);if(error!=null)errors.add(error);}return new Validation<>(input,List.copyOf(errors));}
    public static void main(String[] args){List<Function<String,String>> checks=List.of(s->s==null||s.isBlank()?"required":null,s->s!=null&&s.length()<5?"too short":null,s->s!=null&&!s.contains("@")==true?"missing @":null);
        Validation<String> good=validate("a@b.co",checks);if(!good.valid())throw new AssertionError(good.errors());Validation<String> bad=validate("x",checks);
        if(!bad.errors().equals(List.of("too short","missing @")))throw new AssertionError(bad.errors());
        Validation<String> missing=validate(null,checks);if(!missing.errors().contains("required"))throw new AssertionError("null input");
        System.out.println("Collected validation errors: "+bad.errors());}
}