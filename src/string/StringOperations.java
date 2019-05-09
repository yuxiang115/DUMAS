package string;

public class StringOperations {
    public static String concatenate(String[] strings) {
        StringBuffer valueString = new StringBuffer(200);
        boolean space = false;
        for (int i = 0; i < strings.length; ++i) {
            if (strings[i] == null) continue;
            if (space) {
                valueString.append(" ");
            } else {
                space = true;
            }
            valueString.append(strings[i]);
        }
        return valueString.toString();
    }
}

