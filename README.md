# Spanish Placement Exam Script

This script is to allow student grades on Spanish placement exams in
Canvas to be automatically added to MPathways.  It runs
periodically as a batch script.  It needs little attention unless
there are external changes to authentication information or data
formats.  The script is silent except when there are grades processed.
If there is new grade information the script will email a 
summary report to an Mcommunity group.  The summary report is also
written to the log every time the script runs.

SPE will keep track of the last time it requested grade data so that
grades won't constantly be requested or processed.  If, for some reason,
the stored date isn't
the one needed it can be adjusted manually.  See SPECIAL PROCESSING below
for details.

# Design
SPE is a Spring Boot java application script with the following features.

* It uses a single ESB API with two calls to, first, read grades from the Unizin Data Warehouse and, second, to 
update MPathways with that data.
* Non-secure configuration is bundled with the application in properties files.
* Secure information is provided in separate files available through OpenShift Secrets. 
* Summary information is logged and, when there grade activity, a
  summary is sent to an MCommunity group.
* A very small amount of disk storage is used to record the last request
date.  This prevents continual re-processing of the same scores.
See SPECIAL FEATURES for details.
 
# Running SPE

## Runtime Environment
### Local state
SPE maintains a small amount of state on disk in order to store the last time
that grades were read.
### ESB
There are QA and Production instances of the SpanishPlacementScores API.  See
the configuration files for the exact names.
There is no non-production Unizin Data Warehouse, so for both
production and instances ESB APIs the scores are 
read from the Production Unizin Data Warehouse. The Spanish Placement
test itself was created in our production instance of Canvas.   To
allow for development testing there are two SPE test courses, one
unpublished one for 
development testing and one public one for student use.  The score
updates done by the ESB API QA instance are done to our test MPathways
database and production ESB API updates the production MPathways database.
### QA and Production
The SPE script runs as an OpenShift application.  Non-prod and prod  
Projects and applications have been provisioned in the UM OpenShift instance.  Additional 
instances should not be required.
### Frequency of updates
SPE functions as a batch script.  OpenShift does not, as yet, adequately support
cron like functionality. To make sure that the script runs periodically
the script implements the capacity to wait for configurable periods of time in-between 
processing attempts The wait time is a configurable
property. Eventually this home grown 
wait functionality should be replaced by explicit cron functionality.

# Configuration / Properties

Configuration is done primarily with configuration files.  Properties
can be overriden by environment variables as necessary.  Note that
property changes will require re-deploying the application to pick up
the new values.

## Property files
SPE takes advantage of the Spring *profile* application properties files 
capability. Profiles are property files are named 
using the convention of *application-{profile_name}.properties*.
The file with the name *application.properties* will always be read.  Additional 
files with names following that convention 
are an optional profile properties files.  
The profiles to be read  can be specified by adding the profile name
suffix to the run time 
argument *--spring.profiles.include={profile_name}*. Multiple profile names can
be included. The files will be read in the 
order specified.  A property value may be set in multiple files.  The last value
read will be used. For example the argument *--spring.profiles.include=DBG,OS-DEV* would
include the files *application.properties*, *application-DBG.properties* 
and *application-OS-DEV.properties*.  Any property value set in the OS-DEV
profile file would be the value used by SPE.

Properties are split between secure and public properties. Secure files should
only contain information that isn't appropriate to put in a public GitHub
repository. Public properties are kept with the rest of the files in 
source control.   In OpenShift 
the secure properties are kept in project specific Secrets.  The secrets volume 
will need to be mounted as a seperate directory:  E.g. */opt/secrets*. 

In production there will typically be three properties files used.

 * application.properties - This includes values unlikely to change
 between DEV/QA/PROD instances.
 
 * application-OS-{instance}.properties - This includes only values specific to a 
 particular instance.  E.g. It will include the course number for the test SPE site 
 or for the real SPE site depending on the instance.
 
 * application-{secure-profile}.properties. - This will contain only the 
 information required for secure connections.  E.g. the urls, key, and secret (etc.)
 values to connect to the ESB. 
 
## Overriding properties
Properties can also be specified as environment variables or as command line
arguments.  Most properties will be set in files but it convenient
to override some values at runtime. In particular this is useful to specify the
set of properties files to read. It's also useful if logging levels
need to be adjusted.  

# Development

There are no explicit dependencies in SPE on either Docker or
OpenShift.  If you supply the arguments, data volumes, and services required
by SPE it should run fine in Docker on a laptop or from the command
line or in an IDE.

The code expects a mail server to be available.  A mail server is available
in the UM OpenShift environment.  When running outside OpenShift a debug 
mail server can be started with the command:

<code>    python -m smtpd -d -n -c DebuggingServer localhost:1025 & </code>

Verify that the mail server host and port properties are set properly for
the local environment. 


# OpenShift Considerations

## Logs
When run outside of OpenShift logs are available as expected in the runtime 
environment.  When running in OpenShift
logs are available in the pods created the OpenShift deployment.  The
log names all start with 'spe' and end with a time stamp. If the pod
appears in the Applications/Pods list for the project you can get the
log from the UI. After the pod is terminated old logs should be available 
using the "View on the Archive" tab.  Logs may also be available via 
the command line via *oc* or in Spunk, or at
https://kibana.openshift.dsc.umich.edu/

## Creating an SPE instance
A new instance of code should be based on the deployment and build
configuration of the existing instances.  It is unlikely that a new
instance will be required.  Updating an existing instance is covered
below.

The disk volumes for the date persistence and for the OpenShift
secrets need to be mounted explicitly.  This can be done in deployment
configuration yaml or in the OpenShift UI.

## Updating a SPE instance

A SPE application instance might need to be updated for several reasons.

- *schedule*: The repetition frequency of the job may need to change.
This is done by changing the value of intervalSeconds in the
application properties file or updating the environment variable.
- *new image*: If there is a new build the image specification must be
updated. It shouldn't be "latest" for a production version.  Modifying
this requires editing the deployment configuration yaml.
- *application arguments*:  The list of application property files used by 
the script can
be modified by changing the container arguments in the deployment
configuration yaml. The list will be different for different instances.
See the properties file section above for more details.

# SPECIAL TASKS

## Adjust the last retrieved date

In rare circumstances it may be necessary to reset the last queried
date used by SPE.  The simplest approach is to create a new environment 
variable for a deployment configuration with the required time stamp. 
The variable name is: **getgrades_gradedaftertime** and value 
should be a valid time stamp value in the format **2017-04-01 18:00:00**.  The 
application should then re-deploy and use this value for the next run.  After 
the application runs it is important to delete that environment variable and 
have the application re-deploy again.  The value of the environment
variable will override any other values, so if ithe environment
variable is not deleted and the application re-deployed it will
continue to use the identical timestamp to select grades every time
the script runs.

## Adjust run frequency
The frequency of when SPE runs can be adjusted with properties. See the
properties files for details.
 
