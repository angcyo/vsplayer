package com.xboot.stdcall;

public class posix
{
	public final static int O_RDONLY 	= 0x00000001;
	public final static int O_WRONLY 	= 0x00000002;
	public final static int O_RDWR 		= 0x00000003;
	public final static int O_CREAT 	= 0x00000100;
	public final static int O_EXCL 		= 0x00000200;
	public final static int O_NOCTTY 	= 0x00000400;
	public final static int O_TRUNC		= 0x00001000;
	public final static int O_APPEND 	= 0x00002000;
	public final static int O_NONBLOCK 	= 0x00004000;
	public final static int O_SYNC 		= 0x00010000;
	
	public final static int SEEK_SET 	= 0;
	public final static int SEEK_CUR 	= 1;
	public final static int SEEK_END 	= 2;
	
	static
	{
		System.loadLibrary("posix");
	}
	
    /**
     * get posix's version
     */
    public final static native String version();
    

    /**
     * set posix's error number.
     */
    public final static native void seterrno(int err);
    
    /**
     * get posix's error number.
     */
    public final static native int geterrno();
    
    
    /**
     * open a file with flag of (O_RDWR | O_CREAT) and mode of 0666.
     */
    public final static int open(String file)
    {
    	return open(file, O_RDWR | O_CREAT, 0666);
    }
    
    /**
     * open a file.
     */
    public final static native int open(String file, int flag, int mode);
    
    /**
     * close a file descriptor.
     */
    public final static native boolean close(int fd);

    /**
     * write a string to a file descriptor.
     */
	public final static native int write(int fd, byte[] buf);

    /**
     * read a file descriptor.
     */
    public final static native String read(int fd, int size);
    
    /**
     * set the current position of a file descriptor.
     */
    public final static native long lseek(int fd, long pos, int whence);
    
    /**
     * set the current numeric umask and return the previous umask.
     */
    public final static native int umask(int mask);
    
    /**
     * truncate a file to a specified length.
     */
    public final static native boolean ftruncate(int fd, long len);
    
    /**
     * force write of file with file descriptor to disk.
     */
    public final static native boolean fsync(int fd);
    
    /**
     * change the access permissions of a file.
     */
    public final static native boolean chmod(String path, int mode);
    
    /**
     * change the owner and group id of path to the numeric uid and gid.
     */
    public final static native boolean chown(String path, int uid, int gid);
    
    /**
     * return a duplicate of a file descriptor.
     */
    public final static native int dup(int fd);
    
    /**
     * duplicate file descriptor.
     */
    public final static native void dup2(int fd, int fd2);
    
    
    /**
     * change the current working directory to the specified path.
     */
    public final static native boolean chdir(String dir);
    
    /**
     * return a string representing the current working directory.
     */
    public final static native String getcwd();
    
    /**
     * create a hard link to a file.
     */
    public final static native boolean link(String src, String dst);
    
    /**
     * create a symbolic link.
     */
    public final static native boolean symlink(String src, String dst);
    
    /**
     * remove a file.
     */
    public final static boolean remove(String file)
    {
    	return unlink(file);
    }
    
    /**
     * unlink a file.
     */
    public final static native boolean unlink(String file);
       
    /**
     * return a string representing the path to which the symbolic link points.
     */
    public final static native String readlink(String file);
    
    /**
     * create a directory with 0777 mode.
     */
    public final static boolean mkdir(String dir)
    {
    	return mkdir(dir, 0777);
    }
    
    /**
     * create a directory.
     */
    public final static native boolean mkdir(String dir, int mode);
    
    /**
     * remove a directory.
     */
    public final static native boolean rmdir(String dir);
    
    /**
     * rename a file or directory.
     */
    public final static native boolean rename(String src, String dst);
    
    /**
     * test for access to a file.
     */
    public final static native boolean access(String path, int mode);
    
    
    /**
     * set the current process's user id.
     */
    public final static native boolean setuid(int uid);
    
    /**
     * return the current process's user id.
     */
    public final static native int getuid();

    /**
     * set the current process's group id.
     */
    public final static native boolean setgid(int gid);
    
    /**
     * return the current process's group id.
     */
    public final static native int getgid();
    
    /**
     * make this process a session leader.
     */
    public final static native boolean setpgrp();

    /**
     * return the current process group id.
     */
    public final static native int getpgrp();
    
    /**
     * return the current process's effective user id.
     */
    public final static native int geteuid();
    
    /**
     * return the current process's effective group id.
     */
    public final static native int getegid();
    
    /**
     * return the current process id
     */
    public final static native int getpid();
    
    /**
     * return the parent's process id.
     */
    public final static native int getppid();    
    
    /**
     * call the system call setsid().
     */
    public final static native boolean setsid();
    
    /**
     * call the system call setpgid().
     */
    public final static native boolean setpgid();
    
    /**
     * fork a child process.
     * return 0 to child process and PID of child to parent process.
     */
    public final static native int fork();

    /**
     * decrease the priority of process and return new priority.
     */
    public final static native int nice(int incr);
    
    /**
     * kill a process with a signal.
     */
    public final static native boolean kill(int pid, int sig);
    
    /**
     * exit to the system with specified status, without normal exit processing.
     */
    public final static native void exit(int status);
    
    /**
     * set the process group associated with the terminal given by a fd.
     */
    public final static native boolean tcsetpgrp(int fd, int pgid);
    
    /**
     * return the process group associated with the terminal given by a fd.
     */
    public final static native int tcgetpgrp(int fd);
    
    /**
     * execute the command (a string) in a subshell.
     */
	public final static native int system(String cmd);
	
	
	//smdt add 2012-11-14
	public final static native int poweronoff(
			byte off_h, byte off_m, byte on_h, byte on_m, byte enable,int fd);
	
	public final static native int watchdogenable(byte enable, int fd);

	public final static native int watchdogfeed(int fd);

	public final static native char getfwver(int fd);
	
}
