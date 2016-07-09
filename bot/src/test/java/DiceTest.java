import com.mitchellbosecke.seniorcommander.AbstractTest;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class DiceTest extends AbstractTest {


    @Test
    public void rollD100 (){
        assertTrue(testCommandAndResult("!d100", Pattern.compile("@user, You rolled a \\d{1,2}\\.")));
    }
}
