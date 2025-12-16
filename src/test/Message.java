package test;

import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    public Message(String str){
        this.data = str.getBytes();
        this.asText = str;
        date = new Date();

        double temp;
        try{
            temp = Double.parseDouble(str);
        } catch (NumberFormatException e) {
            temp = Double.NaN;
        }
        this.asDouble = temp;
    }

    public Message(byte[] data){
        this(new String(data));
    }

    public Message(double asDouble){
        this(Double.toString(asDouble));
    }
}
