/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Ant" and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.oddjob.dummy;

// Ant imports

import java.io.File;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.apache.tools.ant.taskdefs.email.Message;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;

/**
 * A task to send SMTP email. This is a refactoring of the SendMail and
 * MimeMail tasks such that both are within a single task.
 *
 * @author Magesh Umasankar
 * @author glenn_twiggs@bmc.com
 * @author steve_l@iseran.com steve loughran
 * @author ehatcher@apache.org Erik Hatcher
 * @author paulo.gaspar@krankikom.de Paulo Gaspar
 * @author roxspring@imapmail.org Rob Oxspring
 * @author <a href="mailto:ishu@akm.ru">Aleksandr Ishutin</a>
 * @author <a href="mailto:levylambert@tiscali-dsl.de">Antoine Levy-Lambert</a>
 * @since Ant 1.5
 * @ant.task name="mail" category="network"
 */
public class DummyEmailTask
     extends Task {
    /** Constant to show that the best available mailer should be used.  */
    public static final String AUTO = "auto";
    /** Constant to allow the Mime mailer to be requested  */
    public static final String MIME = "mime";
    /** Constant to allow the UU mailer to be requested  */
    public static final String UU = "uu";
    /** Constant to allow the plaintext mailer to be requested  */
    public static final String PLAIN = "plain";


    /**
     * Enumerates the encoding constants
     */
    public static class Encoding extends EnumeratedAttribute {
        /**
         * finds the valid encoding values
         *
         * @return a list of valid entries
         */
        public String[] getValues() {
            return new String[] {AUTO, MIME, UU, PLAIN};
        }
    }

    private String encoding = AUTO;
    /** host running SMTP  */
    private String host = "localhost";
    private int port = 25;
    /** subject field  */
    private String subject = null;
    /** any text  */
    private Message message = null;
    /** failure flag */
    private boolean failOnError = true;
    private boolean includeFileNames = false;
    private String messageMimeType = null;
    /** special headers */
    /** sender  */
    private EmailAddress from = null;
    /** replyto */
    private Vector replyToList = new Vector();
    /** TO recipients  */
    private Vector toList = new Vector();
    /** CC (Carbon Copy) recipients  */
    private Vector ccList = new Vector();
    /** BCC (Blind Carbon Copy) recipients  */
    private Vector bccList = new Vector();

    /** file list  */
    private Vector files = new Vector();
    private Vector filesets = new Vector();
    /** Character set for MimeMailer*/
    private String charset = null;
    /** User for SMTP auth */
    private String user = null;
    /** Password for SMTP auth */
    private String password = null;
    /** indicate if the user wishes SSL-TLS */
    private boolean SSL = false;

    /**
     * sets the user for SMTP auth; this requires JavaMail
     * @param user
     * @since ant 1.6
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * sets the password for SMTP auth; this requires JavaMail
     * @param password
     * @since ant 1.6
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * tells if the user needs to send his data over SSL
     * @param SSL
     * @since ant 1.6
     */
    public void setSSL(boolean SSL) {
        this.SSL = SSL;
    }

    /**
     * Allows the build writer to choose the preferred encoding method
     *
     * @param encoding The encoding (one of AUTO,MIME,UU,PLAIN)
     */
    public void setEncoding(Encoding encoding) {
        this.encoding = encoding.getValue();
    }


    /**
     * Sets the mail server port
     *
     * @param port The port to use
     */
    public void setMailport(int port) {
        this.port = port;
    }


    /**
     * Sets the host
     *
     * @param host The host to connect to
     */
    public void setMailhost(String host) {
        this.host = host;
    }


    /**
     * Sets the subject line of the email
     *
     * @param subject Subject of this email.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }


    /**
     * Shorthand method to set the message
     *
     * @param message Message body of this email.
     */
    public void setMessage(String message) {
        if (this.message != null) {
            throw new BuildException("Only one message can be sent in an "
                 + "email");
        }

        this.message = new Message(message);
        this.message.setProject(getProject());
    }


    /**
     * Shorthand method to set the message from a file
     *
     * @param file The file from which to take the message
     */
    public void setMessageFile(File file) {
        if (this.message != null) {
            throw new BuildException("Only one message can be sent in an "
                 + "email");
        }

        this.message = new Message(file);
        this.message.setProject(getProject());
    }


    /**
     * Shorthand method to set type of the text message, text/plain by default
     * but text/html or text/xml is quite feasible.
     *
     * @param type The new MessageMimeType value
     */
    public void setMessageMimeType(String type) {
        this.messageMimeType = type;
    }


    /**
     * Add a message element
     *
     * @param message The message object
     * @throws BuildException if a message has already been added
     */
    public void addMessage(Message message)
         throws BuildException {
        if (this.message != null) {
            throw new BuildException("Only one message can be sent in an "
                 + "email");
        }

        this.message = message;
    }


    /**
     * Adds a from address element
     *
     * @param address The address to send from
     */
    public void addFrom(EmailAddress address) {
        if (this.from != null) {
            throw new BuildException("Emails can only be from one address");
        }

        this.from = address;
    }


    /**
     * Shorthand to set the from address element
     *
     * @param address The address to send mail from
     */
    public void setFrom(String address) {
        if (this.from != null) {
            throw new BuildException("Emails can only be from one address");
        }

        this.from = new EmailAddress(address);
    }


    /**
     * Adds a replyto address element
     *
     * @param address The address to reply to
     * @since ant 1.6
     */
    public void addReplyTo(EmailAddress address) {
        this.replyToList.add(address);
    }


    /**
     * Shorthand to set the replyto address element
     *
     * @param address The address to which replies should be directed
     * @since ant 1.6
     */
    public void setReplyTo(String address) {
        this.replyToList.add(new EmailAddress(address));
    }


    /**
     * Adds a to address element
     *
     * @param address An email address
     */
    public void addTo(EmailAddress address) {
        toList.addElement(address);
    }


    /**
     * Adds "to" address elements
     *
     * @param list Comma separated list of addresses
     */
    public void setToList(String list) {
        StringTokenizer tokens = new StringTokenizer(list, ",");

        while (tokens.hasMoreTokens()) {
            toList.addElement(new EmailAddress(tokens.nextToken()));
        }
    }


    /**
     * Adds "cc" address element
     *
     * @param address The email address
     */
    public void addCc(EmailAddress address) {
        ccList.addElement(address);
    }


    /**
     * Adds "cc" address elements
     *
     * @param list Comma separated list of addresses
     */
    public void setCcList(String list) {
        StringTokenizer tokens = new StringTokenizer(list, ",");

        while (tokens.hasMoreTokens()) {
            ccList.addElement(new EmailAddress(tokens.nextToken()));
        }
    }


    /**
     * Adds "bcc" address elements
     *
     * @param address The email address
     */
    public void addBcc(EmailAddress address) {
        bccList.addElement(address);
    }


    /**
     * Adds "bcc" address elements
     *
     * @param list comma separated list of addresses
     */
    public void setBccList(String list) {
        StringTokenizer tokens = new StringTokenizer(list, ",");

        while (tokens.hasMoreTokens()) {
            bccList.addElement(new EmailAddress(tokens.nextToken()));
        }
    }


    /**
     * Indicates whether BuildExceptions should be passed back to the core
     *
     * @param failOnError The new FailOnError value
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }


    /**
     * Adds a list of files to be attached
     *
     * @param filenames Comma separated list of files
     */
    public void setFiles(String filenames) {
        StringTokenizer t = new StringTokenizer(filenames, ", ");

        while (t.hasMoreTokens()) {
            files.addElement(getProject().resolveFile(t.nextToken()));
        }
    }


    /**
     * Adds a set of files (nested fileset attribute).
     *
     * @param fs The fileset
     */
    public void addFileset(FileSet fs) {
        filesets.addElement(fs);
    }


    /**
     * Sets Includefilenames attribute
     *
     * @param includeFileNames Whether to include filenames in the text of the
     *      message
     */
    public void setIncludefilenames(boolean includeFileNames) {
        this.includeFileNames = includeFileNames;
    }


    /**
     * Identifies whether file names should be included
     *
     * @return Identifies whether file names should be included
     */
    public boolean getIncludeFileNames() {
        return includeFileNames;
    }


    /** Sends an email  */
    public void execute() {
        Message savedMessage = message;
        Vector savedFiles = (Vector) files.clone();

        try {

            // a valid message is required
            if (message == null) {
                message = new Message();
                message.setProject(getProject());
            }

            // an address to send from is required
            if (from == null || from.getAddress() == null) {
                throw new BuildException("A from element is required");
            }

            // at least one address to send to/cc/bcc is required
            if (toList.isEmpty() && ccList.isEmpty() && bccList.isEmpty()) {
                throw new BuildException("At least one of to,cc or bcc must "
                     + "be supplied");
            }

            // set the mimetype if not done already (and required)
            if (messageMimeType != null) {
                if (message.isMimeTypeSpecified()) {
                    throw new BuildException("The mime type can only be "
                         + "specified in one location");
                } else {
                    message.setMimeType(messageMimeType);
                }
            }
            // set the character set if not done already (and required)
            if (charset != null) {
                if (message.getCharset() != null) {
                    throw new BuildException("The charset can only be "
                         + "specified in one location");
                } else {
                    message.setCharset(charset);
                }
            }

            // identify which files should be attached
            Enumeration e = filesets.elements();

            while (e.hasMoreElements()) {
                FileSet fs = (FileSet) e.nextElement();

                DirectoryScanner ds = fs.getDirectoryScanner(getProject());
                String[] includedFiles = ds.getIncludedFiles();
                File baseDir = ds.getBasedir();

                for (int j = 0; j < includedFiles.length; ++j) {
                    File file = new File(baseDir, includedFiles[j]);

                    files.addElement(file);
                }
            }

            // let the user know what's going to happen
            log("Sending email: " + subject, Project.MSG_INFO);
            log("From " + from, Project.MSG_VERBOSE);
            log("ReplyTo " + replyToList, Project.MSG_VERBOSE);
            log("To " + toList, Project.MSG_VERBOSE);
            log("Cc " + ccList, Project.MSG_VERBOSE);
            log("Bcc " + bccList, Project.MSG_VERBOSE);

            // let the user know what happened
            int count = files.size();

            log("Sent email with " + count + " attachment"
                 + (count == 1 ? "" : "s"), Project.MSG_INFO);
        } catch (BuildException e) {
            log("Failed to send email", Project.MSG_WARN);
            if (failOnError) {
                throw e;
            }
        } catch (Exception e) {
          log("Failed to send email", Project.MSG_WARN);
          if (failOnError) {
            throw new BuildException(e);
          }
        } finally {
            message = savedMessage;
            files = savedFiles;
        }
    }
    /**
     * Sets the character set of mail message.
     * Will be ignored if mimeType contains ....; Charset=... substring or
     * encoding is not a <code>mime</code>
     * @since Ant 1.6
     */
    public void setCharset(String charset) {
      this.charset = charset;
    }
    /**
     * Returns the character set of mail message.
     *
     * @return Charset of mail message.
     * @since Ant 1.6
     */
    public String getCharset() {
      return charset;
    }
}

