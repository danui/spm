
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.LinkedList;
import java.util.Iterator;

public class SpmMain {

    public static void main(String[] args) throws Exception {
        new SpmMain(args);
    }

    public SpmMain(String[] args) throws Exception {

        // Change args into an array of files.
        File[] files = getFiles(args);

        // Change files into paths.
        String[] paths = getPaths(files);

        // Get list of GIT commit hashes.
        LinkedList<String> commits = getCommits(paths);

        Iterator<String> iter = commits.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }

    public File[] getFiles(String[] args) throws Exception {
        File[] files = new File[args.length];
        for (int i=0; i<args.length; ++i) {
            files[i] = new File(args[i]);
            if (!files[i].isFile())
                throw new Exception("Not a file: " + args[i]);
        }
        return files;
    }

    public String[] getPaths(File[] files) {
        String[] paths = new String[files.length];
        for (int i = 0; i < files.length; ++i) {
            paths[i] = files[i].getAbsolutePath();
        }
        return paths;
    }

    public LinkedList<String> getCommits(String[] paths) throws Exception {
        Runtime rt = Runtime.getRuntime();
        String[] cmd = new String[5 + paths.length];
        cmd[0] = "git";
        cmd[1] = "log";
        cmd[2] = "--reverse";
        cmd[3] = "--format";
        cmd[4] = "format: %H";
        for (int i = 0; i < paths.length; ++i) {
            cmd[5+i] = paths[i];
        }
        Process child = rt.exec(cmd);
        child.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream()));
        String line;
        LinkedList<String> commits = new LinkedList<String>();
        while (null != (line = reader.readLine())) {
            commits.add(line.trim());
        }
        return commits;
    }



}
