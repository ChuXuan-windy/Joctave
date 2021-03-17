import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//This Class stand for a session in octave,a process will be created when created instance.
//the session will close automatic after the program close;
public class Joctave
{
    private final Process octave;
    private Reader reader;
    private PrintWriter writer;

    private BlockingQueue<String> reply;

    public Joctave(Process octave)
    {
        this.octave = octave;
    }

    public Joctave() throws IOException
    {
        this("octave");
    }
    /*
        Use the Path of the program to init,throw IOException when:
            The operating system program file was not found.
            Access to the program file was denied.
            The working directory does not exist.
     */
    public Joctave(String path) throws IOException
    {
        ProcessBuilder builder = new ProcessBuilder(path,
                "--interactive",
                "--silent",
                "--no-line-editing"
        );
        builder.redirectErrorStream(true);
        reply = new LinkedBlockingQueue<>();
        octave = builder.start();

        reader = new InputStreamReader(octave.getInputStream());
        writer = new PrintWriter(new OutputStreamWriter(octave.getOutputStream()));
        startThread();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        try
        {
            reply.take();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    /*
        execute command like running octave in the shell,return all replying message;
        throw OctaveStopException when
            the process of octave is destroyed.
     */
    public String exec(String command) throws OctaveStopException
    {
        if (!octave.isAlive())
            throw new OctaveStopException(octave);
        String answer = null;
        writer.println(command);
        writer.flush();
        try
        {
            answer = reply.take();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return answer;
    }

    private void startThread()
    {
        Thread t = new Thread(() ->
        {
            try
            {
                StringBuilder builder = new StringBuilder();
                while (octave.isAlive())
                {
                    builder.append((char) reader.read());
                    if (!reader.ready() && builder.charAt(builder.length() - 2) == '>')
                    {
                        //delete the word "octave:?> " or "> " at the end of the output;
                        int index = builder.lastIndexOf("octave:");
                        if (index != -1)
                            builder.delete(index,builder.length());
                        else
                            builder.delete(builder.length() - 2,builder.length());
                        reply.put(builder.toString());
                        builder.delete(0, builder.length());
                    }
                }
                return;
            }
            catch (InterruptedException | IOException e)
            {
                e.printStackTrace();
            }
        });
        t.start();
    }
    //stop the session,it will run automatic after the program close;
    public void stop()
    {
        octave.destroy();
    }
}

class OctaveStopException extends Exception
{
    Process octave;

    public OctaveStopException(Process octave)
    {
        super();
        this.octave = octave;
    }
}