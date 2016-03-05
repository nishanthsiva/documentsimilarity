package utilities;

import java.util.logging.Logger;

/**
 * Created by nishanthsivakumar on 3/5/16.
 */
public class StringUtil {

    private static final Logger LOGGER = Logger.getLogger(StringUtil.class.getName());
    private static String CLASS_NAME = StringUtil.class.getName();

    public static final String removePuncutation(String input){
        final String METHOD_NAME = "removePuncutation";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        input = input.replaceAll("[^A-Za-z0-9]"," ");

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return input;

    }

    public static final String[] processWord(String input){
        final String METHOD_NAME = "processWord";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        String[] processedWords;
        input = removePuncutation(input);
        input = input.toLowerCase();
        processedWords = input.split(" ");

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return processedWords;
    }
}
