                                                                * Git *

Developers can work together from anywhere.
Developers can see the full history and can compare the previous and new changes of the project.
Git is the most popular, open-source, widely used, and an example of distributed version control system (DVCS) used for handling the development of small and large projects in a more efficient and neat manner.
It is most suitable when there are multiple people working on projects as a team and is used for tracking the project changes and efficiently supports the collaboration of the development process.

collebrate:
Clone the repository: Clone the remote repository to your local machine using the git clone command. This creates a local copy of the repository that you can work on.
Collaborate and review changes: Share the branch name with your collaborators, and they can also clone the repository, switch to the branch, and make their own changes. To incorporate their changes into your branch, you can use the git pull command.

bash
Copy code
git pull origin <branch_name>

branch create:
git remote add orgin (branch name) (url)
git push orgin (branch name)

update the file...

git pull --force (https://github.com/durga1808/creation.git)
git commit -a --amend -m "My new commit message"
 
git branch..

git checkout -b (branch name) 

git remote
(list the local and remote branchess)

                                                                         * Example of git creation * 

echo "# order" >> README.md
git init
git add README.md
git commit -m "first commit"
git branch -M main
git remote add origin https://github.com/durga1808/order.git
git push -u origin main


                                                                                    * what is git *

Git is a widely used distributed version control system designed to track changes in source code and collaborate on software development projects.
 It offers numerous benefits that make it an essential tool for developers and teams working on code-based projects. 
Here are some of the key reasons why Git is commonly used:

Version control: Git allows developers to keep track of changes made to their code over time. 
It records every modification, addition, or deletion of files, allowing them to roll back to previous versions if necessary or compare different versions to understand the development history.

Collaboration: Git facilitates collaboration among multiple developers working on the same project.
 It enables seamless merging of code changes from different team members, helping to prevent conflicts and ensuring that everyone is working on the latest version of the code.

Branching and merging: Git provides an efficient and straightforward branching model. 
Developers can create branches to work on specific features or fixes independently. Once a feature is complete, it can be merged back into the main codebase, maintaining a clean and organized project history


                                                                                      *what is version control system *

A version control system (VCS) is a software tool that enables developers to manage changes to source code, documents, and other files in a collaborative and systematic manner.
 It tracks the history of changes made to files over time, allowing users to view, compare, and revert to earlier versions if needed.
 The primary purpose of a version control system is to facilitate collaboration among multiple developers working on the same project while maintaining code integrity and enabling efficient code management.

                                                                                                     * git status *

git status is a command used in the Git version control system. It allows you to view the current status of your Git repository, 

Changes to Tracked Files: Git will list any modified, added, or deleted files that are currently being tracked by Git. 
 * Modified files are the ones with changes that haven't been committed yet. 
 * Added files are new files that have been added to the repository but not committed yet. 
Deleted files are those that have been removed from the working directory, but the removal hasn't been committed yet