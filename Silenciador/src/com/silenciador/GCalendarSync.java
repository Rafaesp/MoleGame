package com.silenciador;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.silenciador.gcalendar.RedirectHandler;



//http://code.google.com/p/google-api-java-client/wiki/AndroidAccountManager
public class GCalendarSync extends Activity{
	
	private String authToken;
	private static final int DIALOG_ACCOUNTS = 0;
	private static final String PREF = "Silent"; //TODO
	private static final String AUTH_TOKEN_TYPE = "something"; //TODO
	private static final int REQUEST_AUTHENTICATE = 0;
	private static HttpTransport transport;

	
	 public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    gotAccount(false);
		  }

	  protected Dialog onCreateDialog(int id) {
		    switch (id) {
		      case DIALOG_ACCOUNTS:
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setTitle("Select a Google account");
		        final AccountManager manager = AccountManager.get(this);
		        final Account[] accounts = manager.getAccountsByType("com.google");
		        final int size = accounts.length;
		        String[] names = new String[size];
		        for (int i = 0; i < size; i++) {
		          names[i] = accounts[i].name;
		        }
		        builder.setItems(names, new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog, int which) {
		            gotAccount(manager, accounts[which]);
		          }
		        });
		        return builder.create();
		    }
		    return null;
	  }
	  
	  private void gotAccount(boolean tokenExpired) {
		    SharedPreferences settings = getSharedPreferences(PREF, 0);
		    String accountName = settings.getString("accountName", null);
		    if (accountName != null) {
		      AccountManager manager = AccountManager.get(this);
		      Account[] accounts = manager.getAccountsByType("com.google");
		      int size = accounts.length;
		      for (int i = 0; i < size; i++) {
		        Account account = accounts[i];
		        if (accountName.equals(account.name)) {
		          if (tokenExpired) {
		            manager.invalidateAuthToken("com.google", this.authToken);
		          }
		          gotAccount(manager, account);
		          return;
		        }
		      }
		    }
		    showDialog(DIALOG_ACCOUNTS);
		  }
	  void gotAccount(final AccountManager manager, final Account account) {
		    SharedPreferences settings = getSharedPreferences(PREF, 0);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putString("accountName", account.name);
		    editor.commit();
		    new Thread() {

		      @Override
		      public void run() {
		        try {
		          final Bundle bundle =
		              manager.getAuthToken(account, AUTH_TOKEN_TYPE, true, null, null).getResult();
		          runOnUiThread(new Runnable() {

		            public void run() {
		              try {
		                if (bundle.containsKey(AccountManager.KEY_INTENT)) {
		                  Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
		                  int flags = intent.getFlags();
		                  flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
		                  intent.setFlags(flags);
		                  startActivityForResult(intent, REQUEST_AUTHENTICATE);
		                } else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
		                  authenticated(bundle.getString(AccountManager.KEY_AUTHTOKEN));
		                }
		              } catch (Exception e) {
		                handleException(e);
		              }
		            }
		          });
		        } catch (Exception e) {
		          handleException(e);
		        }
		      }
		    }.start();
		  }


	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    switch (requestCode) {
	      case REQUEST_AUTHENTICATE:
	        if (resultCode == RESULT_OK) {
	          gotAccount(false);
	        } else {
	          showDialog(DIALOG_ACCOUNTS);
	        }
	        break;
	    }
	  }

		  
		  protected void authenticated(String authToken) {
			    this.authToken = authToken;
			    ((GoogleHeaders) transport.defaultHeaders).setGoogleLogin(authToken);
			    //RedirectHandler.resetSessionId(transport); TODO Doesn't exists.
			    executeRefreshCalendars(); //TODO
		}

		  
		  void handleException(Exception e) {
			    e.printStackTrace();
			    if (e instanceof HttpResponseException) {
			      HttpResponse response = ((HttpResponseException) e).response;
			      int statusCode = response.statusCode;
			      try {
			        response.ignore();
			      } catch (IOException e1) {
			        e1.printStackTrace();
			      }
			      if (statusCode == 401 || statusCode == 403) {
			        gotAccount(true);
			        return;
			      }			      
		}
		}
		  
		  





}
