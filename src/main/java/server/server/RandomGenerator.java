package server.server;

import java.util.function.Predicate;

public interface RandomGenerator {

    default String generateRandomNumber(int board, Predicate<String> predicate) {
        StringBuilder commentID = new StringBuilder();
        do {
            char[] validChars = {'0', '2', '1', '3', '5', '8', '4', '9', '7', '6'};
            for (int i = 0; i < 7; ++i)
                commentID.append(validChars[((int) (Math.random() * 1000000)) % validChars.length]);
        } while (predicate.test(commentID.toString()));
        return commentID.toString();
    }

    default String generateRandomString(int board, Predicate<String> predicate) {
        StringBuilder ID = new StringBuilder();
        do {
            for(int i = 0; i < board; ++i) {
                int x = (((int) (Math.random() * 1000000)) % 75) + 48;
                if(x == 92) {
                    i--;
                    continue;
                }
                ID.append(Character.valueOf((char)x));
            }
        } while (predicate.test(ID.toString()));
        return ID.toString();
    }
}
