# Extra Implementations


### IORedirection

**>>** to append output to given file.
**2>** to redirect errors to given file.

### List

option ls -l added to check for last access time of a file or inside a directory. Leaving the last arg empty will result
in the operation performed on the current directory.

### Strings

syntax = **strings [options] file_name(s)**

When used without any options, strings displays all strings that are at least four characters in length in the files whose 
names are supplied as arguments (i.e., input data). Strings that are on separate lines in the input files are shown on 
separate lines on the screen, and an attempt is made to display all strings found on a single line in a file on a single 
line on the screen (although there may be a carryover to subsequent lines in the event that numerous strings are found on 
a single line).

Perhaps the most commonly used of strings's few options is -n, which, when followed by an integer, 
tells it to return strings which are at least the number of that integer in length. For example, 
the following would display all strings in the files named file1 and file2 that consist of at least two characters:

**strings -n 2 file1 file2**

The -t option tells strings to also return the offset position for each line on which one or more strings are found. 
This option is followed by a letter indicating the numbering system to be used, i.e., o for octal, d for decimal and 
x for hexadecimal. Each printing character, each space and the start of each new line add one to the count. 
Thus, for example, for a file named file3 which contains the string abcd followed by a single space and then 
by the string efghi on the first line and the string jklm on the second line, the following command would return the 
number 0 before the strings in the first line and the number 11 before the string in the second line:

**strings -t d file3**

### Copy

syntax = **cp [options] name new_name**

As a safety precaution, by default cp only copies files and not directories. If a file with the same name as that 
assigned to the copy of a file (or a directory with the same name as that assigned to the copy of a directory) 
already exists, the file (or directory) will not be copied.

Any number of files can be simultaneously copied into another directory by listing their names followed by the 
name of the directory. cp is an intelligent command and knows to do this when only the final argument 
(i.e., piece of input data) is a directory. The files copied into the directory will all have the same names as 
the originals. Thus, for example, the following would copy the files named file2, file3 and file4 into a directory 
named dir1:

**cp file2 file3 file4 dir1**

The -r (i.e., recursive) option, which can also be written with an upper case R, allows directories including all 
of their contents to be copied. (Directories are not copied by default in order to make it more difficult for users to 
accidentally overwrite existing directories which have the same name as that assigned to the copy being made and which 
might contain critical directory structures or important data.) Thus, for example, the following command would make a 
copy of an existing directory called dir2, inclusive of all it contents (i.e., files, subdirectories, their subdirectories,
 etc.), called dir3:

**cp -r dir2 dir3**

### Touch

syntax = **touch [option] file_name(s)**

If a file entered does not exist it is automatically created.

Touch can also be used to change the last access time or last modified time of a file using options 'a' or 'm'.
To set these times the same as another file -r can be used followed by the name of the file. An offset can also be 
introduced by adding either -B for back or -F for forward. In the following way where the offset 200 is in seconds:

**touch -ram file1 -B 200 file2**

### MakeDirectory

syntax = **mkdir [option] directory_name(s)**

This function creates new empty dirs if they don't already exist.

Options include -p and -v. With -p all non-existing parent dirs can also be created for e.g. mkdir -p foo/bar will 
create both foo and bar inside it if foo doesn't exist. -v can be used to get information about the proccess.

### RemoveDirectory

syntax = **rmdir [option] directory_name(s)**

This function removes empty dirs.

Options include -p and -v. With -p all parent dirs can also be removed if they become empty is succession for e.g. 
mkdir -p foo/bar will first delete bar if it is empty followed by foo for the same condition. 
-v can be used to get information about the proccess.

### Remove

syntax = **rm [options] [-r directories] filenames**

This function deleted both dirs and files. It recursively deletes dirs if the -r is used, else it leaves them alone.
-v can be used to get information about the proccess. '--' can be used to mark the end of options so there is no
conflict with files or dirs with names starting with '-'.# Jsh
