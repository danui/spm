package com.apfrank.spm;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.api.LogCommand;


import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * A Project object provides an interface to access information about
 * an SPM Git Directory. This does not know about stories, backlog, etc.
 * We defer that to higher level logic.
 */
public class Project {

    private Git git;
    private String branch;
    private Path projectPath;
    private String projectName;
    
    private File repositoryDir;
    private File projectDir;

    /**
     * Map filename -> TodoFile
     */
    private Map<String,TodoFile> todoFileMap;

    private CommitLog commitLog;
    
    public Project(Git git, String branch, Path path,
                   FilenameFilter filenameFilter)
        throws Exception
    {
        this.git = git;
        this.branch = branch;
        this.projectPath = path;
        repositoryDir = GitTools.getRepositoryDir(git);
        projectDir = projectPath.getFile(repositoryDir);
        projectName = projectPath.toString();
        buildTodoFileMap(filenameFilter);
        buildCommitLog();
    }
    
    public File getRepositoryDir() {
        return repositoryDir;
    }
    
    public File getProjectDir() {
        return projectDir;
    }
    
    public String getName() {
        return projectName;
    }
    
    public void setName(String name) {
        projectName = name;
    }
    
    public int getFileCount() {
        return todoFileMap.size();
    }
    
    /**
     * Get an Iterator over the filenames accepted by the provided
     * FilenameFilter.
     */
    public Iterator<String> getFilenameIterator() {
        return todoFileMap.keySet().iterator();
    }

    public Iterator<TodoFile> getTodoFileIterator() {
        return todoFileMap.values().iterator();
    }
    
    /**
     * Build todoFileMap, which maps:
     *
     * <pre>
     * String filename -> TodoFile
     * </pre>
     */
    private void buildTodoFileMap(FilenameFilter filenameFilter) {
        assert projectDir != null;
        assert todoFileMap == null;
        todoFileMap = new TreeMap<String,TodoFile>();
        SortedSet<String> set = FileTools.findFilenames(
            projectDir, filenameFilter);
        Iterator<String> iter = set.iterator();
        while (iter.hasNext()) {
            String filename = iter.next();
            Path path = new Path(projectPath, filename);
            TodoFile todoFile = new TodoFile(this, path);
            todoFileMap.put(filename, todoFile);
        }
    }

    private void buildCommitLog() throws Exception {
        assert todoFileMap != null;
        assert commitLog == null;
        commitLog = new CommitLog();
        Iterator<TodoFile> iter = getTodoFileIterator();
        while (iter.hasNext()) {
            addCommitsFromTodoFile(iter.next());
        }
    }
    
    private void addCommitsFromTodoFile(TodoFile todoFile) throws Exception {
        LogCommand log = git.log();
        log.add(git.getRepository().resolve(
            "refs/remotes/origin/" + branch));
        log.addPath(todoFile.getPath().toString());
        Iterator<RevCommit> iter = log.call().iterator();
        while (iter.hasNext()) {
            RevCommit revCommit = iter.next();
            Commit commit = commitLog.lookup(revCommit);
            DataPoint dataPoint = new DataPoint(
                commit.getDate(),
                todoFile.getPath()
            );
            commit.addDataPoint(dataPoint);
            todoFile.addDataPoint(dataPoint);
        }
    }
}
