package lab.zlren.test.useragent;

import com.kumkee.userAgent.UserAgent;
import com.kumkee.userAgent.UserAgentParser;

/**
 * @author zlren
 * @date 2017-11-18
 */
public class UserAgentTest {

    public static void main(String[] args) {

        String source = "";

        UserAgentParser userAgentParser = new UserAgentParser();
        UserAgent agent = userAgentParser.parse("");
    }
}
