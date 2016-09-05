@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')

import groovyx.net.http.RESTClient
import groovy.json.*

def ANSI_RESET = "\u001B[0m";
def ANSI_BLACK = "\u001B[30m";
def ANSI_RED = "\u001B[31m";
def ANSI_GREEN = "\u001B[32m";
def ANSI_YELLOW = "\u001B[33m";
def ANSI_BLUE = "\u001B[34m";
def ANSI_PURPLE = "\u001B[35m";
def ANSI_CYAN = "\u001B[36m";
def ANSI_WHITE = "\u001B[37m";

def BLUE_BACKGROUND = "\u001B[48;5;021m"



def password = System.console().readPassword 'Enter password for yusuf.soysal: '
def authString = ("yusuf.soysal:" + password).bytes.encodeBase64().toString()

def branches = new File('branchList.txt')

int max = findMaximumBranchNameLength(branches);

branches.eachLine {
	def issueId = getIssueId(it)
    def branchName = getBranchNameFromLine(it)

    printf("Branch: "  + BLUE_BACKGROUND + " %-" + max + "s"  + ANSI_RESET + " Querying issue " + BLUE_BACKGROUND + issueId + ANSI_RESET+ "'s status  ... ", branchName)
	//print "Branch:" + ANSI_BLUE + branchName + ANSI_RESET + " Querying issue " + ANSI_BLUE + issueId + ANSI_RESET+ "'s status  ... "

	try {
        def json = sendRequest(issueId, authString)

    	
    	if( json.fields.status.name != "Closed" ){
    		println ANSI_RED + "\u2716 " + json.fields.status.name + "!! Assignee is " + json.fields.assignee.name + ANSI_RESET
    	} else {
            println ANSI_GREEN + "\u2714" + ANSI_RESET
        }
    	
	} catch (Exception e) {
	    println ANSI_RED + "\u2716  **** failed to query the branch from jira ****" + ANSI_RESET
	}
}

def findMaximumBranchNameLength(def branches){
    int max = 0;
    branches.eachLine {
        def branchName = getBranchNameFromLine(it)
        if( branchName.length() > max ) {
            max = branchName.length();
        }
    }

    return max
}

def sendRequest(String branchName, String authString) {
    def json = ("http://jiraServerIp.com:8080/rest/api/latest/issue/" + branchName).toURL().getText(requestProperties: [Authorization: 'Basic ' + authString])

    def jsonSlurper = new JsonSlurper()
    return jsonSlurper.parseText(json)
}

def getBranchNameFromLine(String line){
    def originIndex = line.indexOf("origin/");
    return line.substring(originIndex)
}

def getIssueId(String line){
    def index = line.lastIndexOf("/")
    def branchName = line.substring(index+1)

    def firstIndex = branchName.indexOf("-")

    if( firstIndex != branchName.lastIndexOf("-") ){
        branchName = branchName.substring(0, branchName.indexOf("-", firstIndex+1))
    }

    branchName
}
