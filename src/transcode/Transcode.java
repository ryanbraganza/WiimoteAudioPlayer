package transcode;

import java.io.*;

public class Transcode {

    public static File transcode(String filename) {
        try {
            File wav = makeWav(filename);
            File au = makeAu(wav);

            return au;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File makeAu(File wav) throws IOException {
        File file = File.createTempFile("audio", ".au");
        file.deleteOnExit();
        if (!file.delete()) {
            throw new RuntimeException("Did not delete "
                    + file.getAbsolutePath());
        }
        Process p = Runtime.getRuntime().exec(
                new String[] { "sox", wav.getAbsolutePath(), "-c1", "-r2k",
                        "-b8", file.getAbsolutePath() });

        BufferedReader br = new BufferedReader(new InputStreamReader(
                p.getErrorStream()));
        String line = br.readLine();
        while (line != null) {
            System.out.println(line);
            line = br.readLine();
        }

        try {
            if (p.waitFor() != 0) {
                throw new RuntimeException("fail");
            }
        } catch (InterruptedException e) {
            // ignore
        }
        return file;
    }

    private static File makeWav(String filename) throws IOException {
        File wav = File.createTempFile("wav", ".wav");
        wav.deleteOnExit();
        if (!wav.delete()) {
            throw new RuntimeException("Did not delete "
                    + wav.getAbsolutePath());
        }
        Process proc = Runtime.getRuntime()
                .exec(new String[] { "ffmpeg", "-i", filename,
                        wav.getAbsolutePath() });
        BufferedReader br = new BufferedReader(new InputStreamReader(
                proc.getInputStream()));
        String line = br.readLine();
        while (line != null) {
            System.out.println(line);
            line = br.readLine();
        }
        br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        line = br.readLine();
        while (line != null) {
            System.out.println(line);
            line = br.readLine();
        }
        try {
            if (proc.waitFor() != 0) {
                throw new RuntimeException("failure");
            }
        } catch (InterruptedException e) {
            // ignore
        }
        return wav;
    }
}
