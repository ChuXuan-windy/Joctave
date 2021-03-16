import java.io.IOException;
import java.util.Scanner;

public class JoctaveTest
{
    public static void main(String[] args) throws IOException,OctaveStopException
    {
        Joctave joctave = new Joctave();
        Scanner in = new Scanner(System.in);
        while (true)
        {
            String answer = joctave.exec(in.nextLine());
            System.out.println(answer);
        }
    }
}
