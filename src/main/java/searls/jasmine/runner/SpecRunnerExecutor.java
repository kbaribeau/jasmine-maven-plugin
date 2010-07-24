package searls.jasmine.runner;

import java.io.IOException;

import searls.jasmine.model.JasmineResult;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class SpecRunnerExecutor {
	
	private static final long MAX_EXECUTION_MILLIS = 300000; //5 minutes 
	private static final String BUILD_REPORT_JS = "var indent=function(c){for(var b='',a=0;a<c;a++)b+='  ';return b},buildMessages=function(c,b){for(var a='',d=0;d<c.length;d++)a+='\\n'+indent(b)+' * '+c[d].message;return a},buildReport=function(c,b){for(var a='',d=0;d<c.length;d++){var e=c[d];a+='\\n'+indent(b)+(e.type=='suite'?'describe ':'it ')+e.name;if(e.type=='spec'){var f=reporter.results()[e.id];if(f.result=='failed'){a+=' <<< FAILURE!';a+=buildMessages(f.messages,b+1)}}a+=' '+buildReport(e.children,b+1)}return a};buildReport(reporter.suites(),0);";
	private static final String BUILD_CONCLUSION_JS = "var specCount = 0; var failCount=0; for(var key in reporter.results()) { specCount++; if(reporter.results()[key].result == 'failed') failCount++; }; specCount+' specs, '+failCount+' failures'";
	
	public JasmineResult execute(String runnerFile) throws FailingHttpStatusCodeException, IOException {
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3);
		applyQuietIncorrectnessListener(webClient);
		
	    HtmlPage page = webClient.getPage("file://"+runnerFile);
	    waitForRunnerToFinish(page);
	    
	    JasmineResult jasmineResult = new JasmineResult();
	    jasmineResult.setDescription(buildRunnerDescription(page));
	    jasmineResult.setDetails(buildReport(page));

	    webClient.closeAllWindows();
	    
	    return jasmineResult;
	}


	private String buildReport(HtmlPage page) throws IOException {
		ScriptResult report = page.executeJavaScript(BUILD_REPORT_JS);
		return report.getJavaScriptResult().toString();
	}

	private String buildRunnerDescription(HtmlPage page) {
		ScriptResult description = page.executeJavaScript(BUILD_CONCLUSION_JS);
		return description.getJavaScriptResult().toString();
	}


	private void waitForRunnerToFinish(HtmlPage page) {
		int waitInMillis = 500;
		for (int i = 0; i < MAX_EXECUTION_MILLIS/waitInMillis; i++) {
			if(executionFinished(page)) {
				return;
			} else {
        		synchronized (page) {
        			try {
						page.wait(waitInMillis);
					} catch (InterruptedException e) {
						break;
					}
        		}
            }
        }
		if(!executionFinished(page)) {
			throw new IllegalStateException("Attempted to wait for the test to complete processing over the course of "+(MAX_EXECUTION_MILLIS/1000)+" seconds," +
					"but it still appears to be running. Aborting test execution.");
		}
	}

	private Boolean executionFinished(HtmlPage page) {
		ScriptResult result = page.executeJavaScript("reporter.finished");
		return (Boolean) result .getJavaScriptResult();
	}

	private void applyQuietIncorrectnessListener(WebClient webClient) {
		//Disables stuff like this "com.gargoylesoftware.htmlunit.IncorrectnessListenerImpl notify WARNING: Obsolete content type encountered: 'text/javascript'."
		webClient.setIncorrectnessListener(new IncorrectnessListener() {
			public void notify(String arg0, Object arg1) {}
		});
	}


}
