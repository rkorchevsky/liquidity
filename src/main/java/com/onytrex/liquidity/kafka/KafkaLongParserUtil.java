package com.onytrex.liquidity.kafka;

class KafkaLongParserUtil {

    public static String parseStringToLong(long l) {
        final var result = new StringBuilder();
        var shift = 9;

        do {
            if (shift == 0)
                result.append('.');

            var mod = l % 10;
            l = l / 10;
            result.append(mod);
            shift--;
        } while (l != 0);

        if (shift >= 0) {
            while (shift > 0) {
                result.append('0');
                shift--;
            }
            result.append(".0");
        }
        return result.reverse().toString();
    }

    public static long parseToLong(String input) {
        var dot = false;
        var sum = 0L;
        var shift = 9;
        final var charArray = input.toCharArray();
        for(var i = 0; i <  charArray.length && shift > 0; i++) {
            char ch = charArray[i];
            if (ch != '.') {
                var digit = Character.digit(ch, 10);
                sum = sum * 10 + digit;
                if (dot)
                    shift--;
            } else
                dot = true;
        }
        while (shift > 0) {
            sum *= 10;
            shift--;
        }
        return sum;
    }
}
