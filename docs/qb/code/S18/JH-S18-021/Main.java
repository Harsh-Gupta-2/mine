import java.util.ArrayList;
import java.util.List;

public class Main {
    static List<String> parse(String text){List<String> fields=new ArrayList<>();StringBuilder field=new StringBuilder();boolean quoted=false,afterQuote=false;
        for(int i=0;i<text.length();i++){char c=text.charAt(i);if(quoted){if(c=='"'){if(i+1<text.length()&&text.charAt(i+1)=='"'){field.append('"');i++;}else{quoted=false;afterQuote=true;}}else field.append(c);}
            else if(afterQuote){if(c!=',')throw new IllegalArgumentException("characters after closing quote");fields.add(field.toString());field.setLength(0);afterQuote=false;}
            else if(c=='"'){if(field.length()!=0)throw new IllegalArgumentException("quote inside unquoted field");quoted=true;}else if(c==','){fields.add(field.toString());field.setLength(0);}else field.append(c);}
        if(quoted)throw new IllegalArgumentException("unclosed quote");fields.add(field.toString());return List.copyOf(fields);}
    public static void main(String[] args){List<String> row=parse("42,\"Doe, Jane\",\"said \"\"hello\"\"\"");
        if(!row.equals(List.of("42","Doe, Jane","said \"hello\"")))throw new AssertionError(row);
        if(!parse("a,,c").equals(List.of("a","","c")))throw new AssertionError("empty cell");
        try{parse("\"unfinished");throw new AssertionError("unclosed quote");}catch(IllegalArgumentException expected){System.out.println("Unclosed CSV quote rejected.");}
        try{parse("\"quoted\"tail");throw new AssertionError("text after closing quote");}catch(IllegalArgumentException expected){System.out.println("Trailing CSV text rejected.");}
        System.out.println("CSV fields: "+row);}
}