import static j2html.TagCreator.body;
import static j2html.TagCreator.br;
import static j2html.TagCreator.button;
import static j2html.TagCreator.div;
import static j2html.TagCreator.form;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.input;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;
import static j2html.TagCreator.textarea;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.threadPool;
import static spark.Spark.secure;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.jetty.util.thread.ThreadPool;

import spark.Request;
import spark.Response;

public class API {
	

	public static final int API_PORT = 8080;
	public static final int API_THREAD_POOL_MAX = 8;
	public static final int API_THREAD_POOL_MIN = 2;
	public static final int API_THREAD_POOL_TIMEOUT_MS = 30000;
	
	private static final String API_SSL_KEYSTORE_PATH = "crypto/server.keystore";
	private static final String API_SSL_TRUSTSTORE_PATH = "crypto/server.truststore";
	private static final String API_SSL_KEYSTORE_PW = "sparkmeup";
	private static final String API_SSL_TRUSTSTORE_PW = "sparkmeup";

	public static void main(String[] args) {
		
		// set the security provider...
		Security.addProvider(new BouncyCastleProvider());
		
		// set port, https and threadpool config
        threadPool(API_THREAD_POOL_MAX, API_THREAD_POOL_MIN, API_THREAD_POOL_TIMEOUT_MS);
        secure(API_SSL_KEYSTORE_PATH, API_SSL_KEYSTORE_PW, API_SSL_TRUSTSTORE_PATH, API_SSL_TRUSTSTORE_PW, true);
        port(API_PORT);
        
        // setup routing		
        path("/api", () -> {
//        	before((request, response) -> {
//        	    boolean authenticated;
//        	    // ... check if authenticated
//        	    if (!authenticated) {
//        	        halt(401, "You are not welcome here");
//        	    }
//        		// log something
//        		log....
//        	});
        	
        	get("/", (request, response) -> "Blockchain-supported Ledgering API for Decentralized Applications - v1.0");
        	
            path("/contract", () -> {
                get("/:cid", (req, rsp) -> getContract(req, rsp));
                get("/:cid/query-all-tx", (req, rsp) -> getTransactionList(req, rsp));
                get("/:cid/query-tx/:txid", (req, rsp) -> getTransactionDetails(req, rsp));
                get("/:cid/invoke-operation", (req, rsp) -> getInvokeOperation(req, rsp));
                post("/:cid/invoke-operation", (req, rsp) -> {
                	String newTxid = postInvokeOperation(req, rsp);
                	rsp.redirect("query-tx/" + newTxid);
                	return null;
                });
                // missing deploycontract
            });
        });
        
	}
	
	/* ---------------------- API METHODS --------------------- */

	private static String getContract(Request req, Response rsp) {
		
    	String cid = req.params(":cid");
    	
		// execute action
		//TODO
    	
		// return html
    	rsp.status(200);
    	rsp.type("text/html");
		return body().with(
		    h3("Contract ID: " + cid),
		    div().with(
		    	p("Below is all metadata related with the contract:")
		    )
//			      div().with(
//			          model.getAllPosts().stream().map((post) ->
//			                div().with(
//			                        h2(post.getTitle()),
//			                        p(post.getContent()),
//			                        ul().with(post.getCategories().stream().map((category) ->
//			                              li(category)).collect(Collectors.toList())
//			                        )
//			                )
//			          ).collect(Collectors.toList())
//			      )
		).render();
	}
	
	private static String getTransactionList(Request req, Response rsp) {

    	String cid = req.params(":cid");
		// execute action
    	//TODO
		
		// return html
    	rsp.status(200);
    	rsp.type("text/html");
		return body().with(
		    h3("Contract ID: " + cid),
		    div().with(
		    	p("Below is a list of transactions/operations related with the contract:")
		    )
		).render();
	}
	
	private static String getTransactionDetails(Request req, Response rsp) {
		

    	String cid = req.params(":cid");
    	String txid = req.params(":txid");
		// execute action
    	//TODO
		
		// return html
    	rsp.status(200);
    	rsp.type("text/html");
		return body().with(
		    h3("Transaction ID: " + txid),
		    div().with(
		    	p("Below is all data related with the specified transaction:")
		    )
		).render();
	}
	
	private static String getInvokeOperation(Request req, Response rsp) {
		
		String cid = req.params(":cid");
		// execute action
		// TODO
		
		// return html
    	rsp.status(200);
    	rsp.type("text/html");
		return body().with(
		    h3("Contract ID: " + cid),
		    div().with(
		    	p("Fill in invocation details. View contract details if you need to know what operations are supported:"),
		    	br(),
		    	form()
		    	.withMethod("POST")
		    	.withAction("invoke-operation")
		    	.with(
				    	// operation id
				    	span("Operation to execute: "),
				    	input()
				    	.withType("text")
				    	.withId("operationId")
				    	.withName("operationId")
				    	.withPlaceholder("e.g. BuyNewCar")
				    	.isRequired(),
				    	br(),
				    	
				    	// transaction / data details
				    	div("Transactional data to be used by the operation (JSON format): "),
				    	textarea()
				    	.attr("rows", 20)
				    	.attr("cols", 70)
				    	.withId("transactionData")
				    	.withName("transactionData")
				    	.withPlaceholder("e.g. \n\n{\n\tbrand: 'Fiat',\n\tmodel: '500', \n\tunits: 1"
				    			+ "\n\tcar-id: ['u8d923-da8313-28mc3-km093i'], \n\tpayment-details: {\n\t\tamount-to-be-payed: '30000',"
				    			+ "\n\t\tcurrency: 'euro', \n\t\tamount-paying: '30000', \n\t\tpayment-method: 'credit-card',"
				    			+ "\n\t\tpayment-policy: '100%'\n\t}\n}")
				    	.isRequired(),
				    	
				    	// submit
				    	br(),
				    	button("Invoke")
				    	.withType("submit")
		    	)
		    )
		).render();
	}
	
	private static String postInvokeOperation(Request req, Response rsp) {

    	String cid = req.params(":cid");
    	String oid = req.queryParams("operationId");
    	String tx = req.queryParams("transactionData");
    	
    	// TODO
    	String txid = "3";// REMOVE
    	
    	return txid;
	}
	
	private static boolean shouldReturnHtml(Request request) {
	    String accept = request.headers("Accept");
	    return accept != null && accept.contains("text/html");
	}


}
