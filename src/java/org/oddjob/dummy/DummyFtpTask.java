/*
 * Copyright (c) 2005, Rob Gordon.
 */
package org.oddjob.dummy;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DummyFileSet;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.FileSelector;

/**
 * 
 * @author Rob Gordon.
 */
public class DummyFtpTask extends Task {

    protected static final int SEND_FILES = 0;

    protected static final int GET_FILES = 1;

    protected static final int DEL_FILES = 2;

    protected static final int LIST_FILES = 3;

    protected static final int MK_DIR = 4;

    protected static final int CHMOD = 5;

    protected static final int RM_DIR = 6;

    /** return code of ftp - not implemented in commons-net version 1.0 */
    private static final int CODE_521 = 521;

    /** Default port for FTP */
    public static final int DEFAULT_FTP_PORT = 21;

    private String remotedir;

    private String server;

    private String userid;

    private String password;

    private File listing;

    private boolean binary = true;

    private boolean passive = false;

    private boolean verbose = false;

    private boolean newerOnly = false;

    private boolean timeDiffAuto = false;

    private long timeDiffMillis = 0;

    private int action;

    private Vector filesets = new Vector();

    private Vector dirCache = new Vector();

    private int transferred = 0;

    private String remoteFileSep = "/";

    private int port = DEFAULT_FTP_PORT;

    private boolean skipFailedTransfers = false;

    private int skipped = 0;

    private boolean ignoreNoncriticalErrors = false;

    private boolean preserveLastModified = false;

    private String chmod = null;

    private String umask = null;

    protected static final String[] ACTION_STRS = { "sending", "getting",
            "deleting", "listing", "making directory", "chmod", "removing" };

    protected static final String[] COMPLETED_ACTION_STRS = { "sent",
            "retrieved", "deleted", "listed", "created directory",
            "mode changed", "removed" };

    protected static final String[] ACTION_TARGET_STRS = { "files", "files",
            "files", "files", "directory", "files", "directories" };

    /**
     * Sets the remote directory where files will be placed. This may be a
     * relative or absolute path, and must be in the path syntax expected by the
     * remote server. No correction of path syntax will be performed.
     * 
     * @param dir
     *            the remote directory name.
     */
    public void setRemotedir(String dir) {
        this.remotedir = dir;
    }

    /**
     * Sets the FTP server to send files to.
     * 
     * @param server
     *            the remote server name.
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * Sets the FTP port used by the remote server.
     * 
     * @param port
     *            the port on which the remote server is listening.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets the login user id to use on the specified server.
     * 
     * @param userid
     *            remote system userid.
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * Sets the login password for the given user id.
     * 
     * @param password
     *            the password on the remote system.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * If true, uses binary mode, otherwise text mode (default is binary).
     * 
     * @param binary
     *            if true use binary mode in transfers.
     */
    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    /**
     * Specifies whether to use passive mode. Set to true if you are behind a
     * firewall and cannot connect without it. Passive mode is disabled by
     * default.
     * 
     * @param passive
     *            true is passive mode should be used.
     */
    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    /**
     * Set to true to receive notification about each file as it is transferred.
     * 
     * @param verbose
     *            true if verbose notifications are required.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * A synonym for <tt>depends</tt>. Set to true to transmit only new or
     * changed files.
     * 
     * See the related attributes timediffmillis and timediffauto.
     * 
     * @param newer
     *            if true only transfer newer files.
     */
    public void setNewer(boolean newer) {
        this.newerOnly = newer;
    }

    /**
     * number of milliseconds to add to the time on the remote machine to get
     * the time on the local machine.
     * 
     * use in conjunction with <code>newer</code>
     * 
     * @param timeDiffMillis
     *            number of milliseconds
     * 
     * @since ant 1.6
     */
    public void setTimeDiffMillis(long timeDiffMillis) {
        this.timeDiffMillis = timeDiffMillis;
    }

    /**
     * &quot;true&quot; to find out automatically the time difference between
     * local and remote machine.
     * 
     * This requires right to create and delete a temporary file in the remote
     * directory.
     * 
     * @param timeDiffAuto
     *            true = find automatically the time diff
     * 
     * @since ant 1.6
     */
    public void setTimeDiffAuto(boolean timeDiffAuto) {
        this.timeDiffAuto = timeDiffAuto;
    }

    /**
     * Set to true to preserve modification times for "gotten" files.
     * 
     * @param preserveLastModified
     *            if true preserver modification times.
     */
    public void setPreserveLastModified(boolean preserveLastModified) {
        this.preserveLastModified = preserveLastModified;
    }

    /**
     * Set to true to transmit only files that are new or changed from their
     * remote counterparts. The default is to transmit all files.
     * 
     * @param depends
     *            if true only transfer newer files.
     */
    public void setDepends(boolean depends) {
        this.newerOnly = depends;
    }

    /**
     * Sets the remote file separator character. This normally defaults to the
     * Unix standard forward slash, but can be manually overridden using this
     * call if the remote server requires some other separator. Only the first
     * character of the string is used.
     * 
     * @param separator
     *            the file separator on the remote system.
     */
    public void setSeparator(String separator) {
        remoteFileSep = separator;
    }

    /**
     * Sets the file permission mode (Unix only) for files sent to the server.
     * 
     * @param theMode
     *            unix style file mode for the files sent to the remote system.
     */
    public void setChmod(String theMode) {
        this.chmod = theMode;
    }

    /**
     * Sets the default mask for file creation on a unix server.
     * 
     * @param theUmask
     *            unix style umask for files created on the remote server.
     */
    public void setUmask(String theUmask) {
        this.umask = theUmask;
    }

    /**
     * A set of files to upload or download
     * 
     * @param set
     *            the set of files to be added to the list of files to be
     *            transferred.
     */
    public void addFileset(DummyFileSet set) {
        filesets.addElement(set);
    }

    /**
     * Sets the FTP action to be taken. Currently accepts "put", "get", "del",
     * "mkdir" and "list".
     * 
     * @deprecated setAction(String) is deprecated and is replaced with
     *             setAction(FTP.Action) to make Ant's Introspection mechanism
     *             do the work and also to encapsulate operations on the type in
     *             its own class.
     * @ant.attribute ignore="true"
     * 
     * @param action
     *            the FTP action to be performed.
     * 
     * @throws BuildException
     *             if the action is not a valid action.
     */
    public void setAction(String action) throws BuildException {
        log("DEPRECATED - The setAction(String) method has been deprecated."
                + " Use setAction(FTP.Action) instead.");

        Action a = new Action();

        a.setValue(action);
        this.action = a.getAction();
    }

    /**
     * Sets the FTP action to be taken. Currently accepts "put", "get", "del",
     * "mkdir", "chmod" and "list".
     * 
     * @param action
     *            the FTP action to be performed.
     * 
     * @throws BuildException
     *             if the action is not a valid action.
     */
    public void setAction(Action action) throws BuildException {
        this.action = action.getAction();
    }

    /**
     * The output file for the "list" action. This attribute is ignored for any
     * other actions.
     * 
     * @param listing
     *            file in which to store the listing.
     */
    public void setListing(File listing) {
        this.listing = listing;
    }

    /**
     * If true, enables unsuccessful file put, delete and get operations to be
     * skipped with a warning and the remainder of the files still transferred.
     * 
     * @param skipFailedTransfers
     *            true if failures in transfers are ignored.
     */
    public void setSkipFailedTransfers(boolean skipFailedTransfers) {
        this.skipFailedTransfers = skipFailedTransfers;
    }

    /**
     * set the flag to skip errors on directory creation. (and maybe later other
     * server specific errors)
     * 
     * @param ignoreNoncriticalErrors
     *            true if non-critical errors should not cause a failure.
     */
    public void setIgnoreNoncriticalErrors(boolean ignoreNoncriticalErrors) {
        this.ignoreNoncriticalErrors = ignoreNoncriticalErrors;
    }

    /**
     * Checks to see that all required parameters are set.
     * 
     * @throws BuildException
     *             if the configuration is not valid.
     */
    protected void checkConfiguration() throws BuildException {
        if (server == null) {
            throw new BuildException("server attribute must be set!");
        }
        if (userid == null) {
            throw new BuildException("userid attribute must be set!");
        }
        if (password == null) {
            throw new BuildException("password attribute must be set!");
        }

        if ((action == LIST_FILES) && (listing == null)) {
            throw new BuildException("listing attribute must be set for list "
                    + "action!");
        }

        if (action == MK_DIR && remotedir == null) {
            throw new BuildException("remotedir attribute must be set for "
                    + "mkdir action!");
        }

        if (action == CHMOD && chmod == null) {
            throw new BuildException("chmod attribute must be set for chmod "
                    + "action!");
        }
    }

    /**
     * Sends all files specified by the configured filesets to the remote
     * server.
     *
     * @param ftp the FTPClient instance used to perform FTP actions
     *
     * @throws IOException if there is a problem reading a file
     * @throws BuildException if there is a problem in the configuration.
     */
    protected void transferFiles()
         throws BuildException {
        transferred = 0;
        skipped = 0;

        if (filesets.size() == 0) {
            throw new BuildException("at least one fileset must be specified.");
        } else {
            // get files from filesets
            for (int i = 0; i < filesets.size(); i++) {
                DummyFileSet fs = (DummyFileSet) filesets.elementAt(i);

                if (fs != null) {
                    log(fs.toString());
                }
            }
        }
        
		if (Math.random() > 0.8) {
			
			throw new BuildException("Pretending FTP Failed Exception");
		}

        log(transferred + " " + ACTION_TARGET_STRS[action] + " "
            + COMPLETED_ACTION_STRS[action]);
    }

    /**
     * Runs the task.
     * 
     * @throws BuildException
     *             if the task fails or is not configured correctly.
     */
    public void execute() throws BuildException {
        checkConfiguration();

        try {
            log("Opening FTP connection to " + server, Project.MSG_VERBOSE);
            log("...pretending to connect.");
            log("connected", Project.MSG_VERBOSE);
            log("logging in to FTP server", Project.MSG_VERBOSE);
            log("login succeeded", Project.MSG_VERBOSE);
            
            if (binary) {
                log("...pretending to change mode to binary");
            } else {
                log("...pretending to change mode to ascii");
            }
            
            if (passive) {
                log("entering passive mode", Project.MSG_VERBOSE);
            }

            log("...pretending to do action [" 
                    + ACTION_STRS[action] + "]");
            // If the action is MK_DIR, then the specified remote
            // directory is the directory to create.

            if (action == MK_DIR) {
                log("...pretending to make remote dir [" + remotedir + "].");
            } else {
                if (remotedir != null) {
                    log("changing the remote directory", Project.MSG_VERBOSE);
                    log("...pretending to change to remote dir [" + remotedir
                            + "]");
                }
                log(ACTION_STRS[action] + " " + ACTION_TARGET_STRS[action]);
                transferFiles();
            }

        } finally {
            log("disconnecting", Project.MSG_VERBOSE);
        }
    }

    /**
     * an action to perform, one of "send", "put", "recv", "get", "del",
     * "delete", "list", "mkdir", "chmod", "rmdir"
     */
    public static class Action extends EnumeratedAttribute {

        private static final String[] VALID_ACTIONS = { "send", "put", "recv",
                "get", "del", "delete", "list", "mkdir", "chmod", "rmdir" };

        /**
         * Get the valid values
         * 
         * @return an array of the valid FTP actions.
         */
        public String[] getValues() {
            return VALID_ACTIONS;
        }

        /**
         * Get the symbolic equivalent of the action value.
         * 
         * @return the SYMBOL representing the given action.
         */
        public int getAction() {
            String actionL = getValue().toLowerCase(Locale.US);

            if (actionL.equals("send") || actionL.equals("put")) {
                return SEND_FILES;
            } else if (actionL.equals("recv") || actionL.equals("get")) {
                return GET_FILES;
            } else if (actionL.equals("del") || actionL.equals("delete")) {
                return DEL_FILES;
            } else if (actionL.equals("list")) {
                return LIST_FILES;
            } else if (actionL.equals("chmod")) {
                return CHMOD;
            } else if (actionL.equals("mkdir")) {
                return MK_DIR;
            } else if (actionL.equals("rmdir")) {
                return RM_DIR;
            }
            return SEND_FILES;
        }
    }
    
    private void logFileSet(FileSet fs) {
        StringBuffer text = new StringBuffer();
        text.append("FileSelectors:");
        Enumeration e = fs.selectorElements();
        while (e.hasMoreElements()) {
            text.append("\n");
            FileSelector selector = (FileSelector) e.nextElement();
            text.append(selector.toString());
        }
        log(text.toString());
    }
    
}