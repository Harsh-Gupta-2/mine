public class Main {
    enum Status { CREATED, PAID, SHIPPED, CANCELLED }
    static final class Order {
        private Status status=Status.CREATED;
        void transition(Status next){boolean valid=switch(status){case CREATED->next==Status.PAID||next==Status.CANCELLED;case PAID->next==Status.SHIPPED||next==Status.CANCELLED;case SHIPPED,CANCELLED->false;};if(!valid)throw new IllegalStateException(status+" -> "+next);status=next;}
        Status status(){return status;}
    }
    public static void main(String[] args){Order order=new Order();order.transition(Status.PAID);order.transition(Status.SHIPPED);
        if(order.status()!=Status.SHIPPED)throw new AssertionError("state");
        try{order.transition(Status.CANCELLED);throw new AssertionError("terminal transition accepted");}catch(IllegalStateException expected){System.out.println("Illegal terminal transition rejected.");}
        Order cancelled=new Order();cancelled.transition(Status.CANCELLED);if(cancelled.status()!=Status.CANCELLED)throw new AssertionError("cancel");
        System.out.println("Valid order lifecycle reached: "+order.status());}
}