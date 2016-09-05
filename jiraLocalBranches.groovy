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


def password = System.console().readPassword 'Enter password for yusuf.soysal: '
def authString = ("yusuf.soysal:" + password).bytes.encodeBase64().toString()

def workingDir = "/Users/yusufsoysal/Development/dmall";

def branches = getBranchList(workingDir)

branches.eachLine {
    it = it.trim()
	def index = it.lastIndexOf("/")
	def issueId = it.substring(index+1)

    def firstIndex = issueId.indexOf("-")

    if( firstIndex != issueId.lastIndexOf("-") ){
        issueId = issueId.substring(0, issueId.indexOf("-", firstIndex+1))
    }
	
	print "Querying issue " + issueId + "'s status for branch " + it + "... "
	try {
        def json = sendRequest(issueId, authString)

        if( json.fields.status.name != "Closed" ){
            println ANSI_RED + "\u2716 " + json.fields.status.name + "!! Assignee is " + json.fields.assignee.name
        } else {
            println ANSI_GREEN + "\u2714"
            println "deleting branch... " + removeBranch(workingDir, it)
        }

    	println " [" + json.fields.summary + "]\n" + ANSI_RESET
	} catch (Exception e) {
        println ANSI_RED + "\u2716  **** failed ****" + ANSI_RESET + "\n"
	}
}

def getBranchList(String workingDir){
    final processBuilder = new ProcessBuilder("git", "branch")
    processBuilder.directory(new File(workingDir))
    Process p = processBuilder.start()

    p.getInputStream().text

}

def removeBranch(String workingDir, String branch){
    final processBuilder = new ProcessBuilder("git", "branch", "-d", branch)
    processBuilder.directory(new File(workingDir))
    Process p = processBuilder.start()

    InputStream errorStream = p.getErrorStream()
    if( errorStream != null ){
        println errorStream.text
    }

    p.getInputStream().text
}

def sendRequest(String branchName, String authString) {
    def json = ("http://jira.n11.com:8080/rest/api/latest/issue/" + branchName).toURL().getText(requestProperties: [Authorization: 'Basic ' + authString])

    def jsonSlurper = new JsonSlurper()
    return jsonSlurper.parseText(json)
}

