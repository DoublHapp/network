package Client;

public class MessageHandle {
 public static boolean AuthRequire_Handle(String response){
        String[] buff =response.split(" ");
       return buff[0].equals("500");
    }
    public static boolean OK_Handle(String response){
        String[] buff =response.split(" ");
        return buff[0].equals("525");
    }
    public static boolean ERROR_Handle(String response){
        String[] buff =response.split(" ");
        return buff[0].equals("401");
    }
    public static double AMNT_Handle(String response){
        String[] buff =response.split(":");
        if(buff[0].equals("AMNT")){
          return Double.parseDouble(buff[1]);
        }
        else
            return -1;
    }
   public  static boolean BYE_Handle(String response){
       return response.equals("BYE");
   }
}
