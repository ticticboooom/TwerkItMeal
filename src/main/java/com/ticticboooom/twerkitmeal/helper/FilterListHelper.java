package com.ticticboooom.twerkitmeal.helper;

import com.ticticboooom.twerkitmeal.config.TwerkConfig;

import java.util.ArrayList;
import java.util.List;

public class FilterListHelper {
    public static boolean shouldAllow(String key) {
        if (TwerkConfig.blackList.size() <= 0) {
            return true;
        }

        List<String> variations = new ArrayList<>();
        // entire block RL
        variations.add(key);
        // mod id from RL of block
        variations.add(key.substring(0, key.indexOf(":") - 1));
        for (String listed : TwerkConfig.blackList) {
            if (variations.contains(listed)) {
                return false;
            }
        }

        if (!TwerkConfig.useWhitelist) {
            return true;
        }

        for (String listed : TwerkConfig.whitelist) {
            if (variations.contains(listed)){
                return true;
            }
        }
        return true;
    }
}
