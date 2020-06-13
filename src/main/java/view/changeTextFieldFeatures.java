package view;

public interface changeTextFieldFeatures {
    default String removeDots(String text) {
        StringBuilder stringBuilder = new StringBuilder(text);
        boolean foundDot = false;
        int textSize = text.length();

        for (int i = 0; i < textSize; i++) {
            if(text.charAt(i) < 48 || text.charAt(i) > 57) {
                if(text.charAt(i) == '.') {
                    if(foundDot) {
                        stringBuilder.deleteCharAt(i);
                        textSize--;
                    }
                    foundDot = true;
                } else {
                    stringBuilder.deleteCharAt(i);
                    textSize--;
                }
            }
        }

        return stringBuilder.toString();
    }
}
