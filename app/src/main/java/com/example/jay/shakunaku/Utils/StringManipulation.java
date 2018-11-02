package com.example.jay.shakunaku.Utils;

public class StringManipulation {

    public static String expandUsername(String username){
        String formatUsername = "@" + username;
        return formatUsername.replace("_"," ").toLowerCase();
    }

    public static String condenseUsername(String username){
        String formatUsername = "@" + username;
        return formatUsername.replace(" ", "_").toLowerCase();
    }

    public static String getTags(String caption){
        if(caption.indexOf("#") > 0){
            StringBuilder sb = new StringBuilder();
            char[] charArray = caption.toCharArray();
            boolean foundWord = false;
            for(char c : charArray){
                if(c == '#'){
                    foundWord = true;
                    sb.append(c);
                }else{
                    if(foundWord){
                        sb.append(c);
                    }
                }

                if(c == ' '){
                    foundWord = false;
                }
            }

            String s = sb.toString().replace(" ", "").replace("#", ",#");
            return s.substring(1, s.length());
        }

        //I think this should return null
        return caption;
    }
}
