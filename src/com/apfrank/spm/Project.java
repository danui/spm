package com.apfrank.spm;

import org.eclipse.jgit.api.Git;

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
    
    private File repositoryDir;
    private File projectDir;

    /**
     * Map filename -> TodoFile
     */
    private Map<String,TodoFile> todoFileMap;

    public Project(Git git, String branch, Path path,
                   FilenameFilter filenameFilter)
    {
        this.git = git;
        this.branch = branch;
        this.projectPath = path;
        repositoryDir = GitTools.getRepositoryDir(git);
        projectDir = projectPath.getFile(repositoryDir);
        initTodoFileMap(filenameFilter);
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

    private void initTodoFileMap(FilenameFilter filenameFilter) {
        assert projectDir != null;
        assert todoFileMap == null;
        todoFileMap = new TreeMap<String,TodoFile>();
        SortedSet<String> set = FileTools.findFilenames(
            projectDir, filenameFilter);
        Iterator<String> iter = set.iterator();
        while (iter.hasNext()) {
            String filename = iter.next();
            TodoFile todoFile = new TodoFile(filename);
            todoFileMap.put(filename, todoFile);
        }
    }

}
