# Code Contributions and Code Reviews

#### Focused Commits

Grade: Very Good

Feedback: 
- The repository has a good amount of commits. 
- 345fb98d is a model for a good commit size. 
- 53666889 is already too big. 
- Commit messages are generally concise. However, the message for 6a236be8 could indicate that too many changes were done in a single commit 


#### Isolation

Grade: Excellent

Feedback: Excellent number of MRs and use of Gitlab futures for merging. Please find better names for the branches. 


#### Reviewability

Grade: Good

Feedback: It's very good that the MRs *usually* have self-explaining titles that are complemented by the description. 
In general, the reviewability of your merge requests is good. 
Some branches are left open for too long.

#### Code Reviews

Grade: Sufficient

Feedback: 
-There is no discussion regarding the MRs
-Looks like there is not too much code reviewing, other than the "approve". (The need to revert 92636bf4 might be a good indication of this issue.)


#### Build Server

Grade: Good

Feedback: Most builds do not fail and take a reasonable amount of time. The failing ones were fixed. I couldn't find the 10+ checkstyle rules.

