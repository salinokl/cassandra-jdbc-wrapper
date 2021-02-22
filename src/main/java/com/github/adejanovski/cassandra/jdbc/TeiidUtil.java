package com.github.adejanovski.cassandra.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeiidUtil {
    static String convertTeiidSql(String input) {
        if (input == null || input.isEmpty())
            return input;

        // 패턴매칭 문자열 치환: ex) "{name1}"."{name2}"."{name3}"."{name4}" ==> "{name4}"
        String columnPattern = "(?<=([,\\s\\t]+))(\"[^\"]+\"\\.\"[^\"]+\"\\.\"[^\"]+\"\\.(\"[^\"]+\"))(?=(([,\\s\\t]+)|$))";

        final String placeHolder = "%encore-placholder%";
        String result = input.replaceAll(columnPattern, placeHolder);

        List<String> replacementList = new ArrayList<String>();
        Pattern p = Pattern.compile(columnPattern);
        if (p == null)
            return input;

        Matcher m = p.matcher(input);
        if (m == null)
            return input;

        while (m.find()) {
            replacementList.add(m.group(3));
        }

        for (String replacment : replacementList) {
            result = result.replaceFirst(placeHolder, replacment);
        }


        input = result;

        // 패턴매칭 문자열 치환: ex) "{name1}"."{name2}"."{name3}" ==> "{name3}"
        String tablePattern =  "(?<=([,\\s\\t]+))(\"[^\"]+\"\\.\"[^\"]+\"\\.(\"[^\"]+\"))(?=(([,\\s\\t]+)|$))";

        result = input.replaceAll(tablePattern, placeHolder);

        replacementList = new ArrayList<String>();
        p = Pattern.compile(tablePattern);
        if (p == null)
            return input;

        m = p.matcher(input);
        if (m == null)
            return input;

        while (m.find()) {
            replacementList.add(m.group(3));
        }

        for (String replacment : replacementList) {
            result = result.replaceFirst(placeHolder, replacment);
        }

        String upperCaseSql = result.toUpperCase();
        if (upperCaseSql.contains("SELECT") == true && upperCaseSql.contains("ALLOW FILTERING") == false)
            result += " ALLOW FILTERING";

        return result;
    }
}
