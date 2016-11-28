function com_zimbra_rocket_HandlerObject() {
   com_zimbra_rocket_HandlerObject.settings = {};
};

com_zimbra_rocket_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_rocket_HandlerObject.prototype.constructor = com_zimbra_rocket_HandlerObject;
var ZimbraRocketZimlet = com_zimbra_rocket_HandlerObject;

ZimbraRocketZimlet.prototype.init = function () {
   ZimbraRocketZimlet.version=this._zimletContext.version;
};

/* status method show a Zimbra status message
 * */
ZimbraRocketZimlet.prototype.status = function(text, type) {
   var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT ];
   appCtxt.getAppController().setStatusMsg(text, type, null, transitions);
}; 

/* Called when the panel is double-clicked.
 */
ZimbraRocketZimlet.prototype.doubleClicked = function() {
   this.singleClicked();
};

/* Called when the panel is single-clicked.
 */
ZimbraRocketZimlet.prototype.singleClicked = function() {
   try {
      if(!this.ZimbraRocketTab)
      {
         this.ZimbraRocketTab = this.createApp("Rocket", "", "Rocket");
         var zimletInstance = appCtxt._zimletMgr.getZimletByName('com_zimbra_rocket').handlerObject;
         var app = appCtxt.getApp(this.ZimbraRocketTab);
         app.activate(true, this.ZimbraRocketTab);
         app.setContent('<div style="position: fixed; left: 0; right: 0; top: 107px; bottom: 0; border:0px;"><iframe id="ZimbraRocketFrame" style="z-index:2; left:0; width:100%; height:100%; border:0px;" src=\"https://YOUR-ROCHET-CHAT\"></div>');
         var overview = app.getOverview(); // returns ZmOverview
         overview.setContent("&nbsp;");
         var child = document.getElementById(overview._htmlElId);
         child.parentNode.removeChild(child);
      
         var toolbar = app.getToolbar(); // returns ZmToolBar
         toolbar.dispose();
         app.launch();    
      }
      else
      {
         var app = appCtxt.getApp(this.ZimbraRocketTab);
         app.launch();
      }
   } catch (err) { console.log (err)} 
}; 

/**
 * This method gets called by the Zimlet framework each time the application is opened or closed.
 *  
 * @param	{String}	appName		the application name
 * @param	{Boolean}	active		if true, the application status is open; otherwise, false
 */
ZimbraRocketZimlet.prototype.appActive =
function(appName, active) {
	if (active)
   {
      //In the Zimbra tab hide the left menu bar that is displayed by default in Zimbra, also hide the mini calendar
      document.getElementById('z_sash').style.display = "none";   
      //Users that click the tab directly after logging in, will still be served with the calendar, as it is normal
      //it takes some time to be displayed, so if that occurs, try to remove the calender again after 10 seconds.
      try {
         var cal = document.getElementsByClassName("DwtCalendar");
         cal[0].style.display = "none";
      } catch (err) { setTimeout(function(){var cal = document.getElementsByClassName("DwtCalendar"); cal[0].style.display = "none"; }, 10000); }
   }
   else
   {
      document.getElementById('z_sash').style.display = "block";
      try {
         var cal = document.getElementsByClassName("DwtCalendar");
         cal[0].style.display = "block";
      } catch (err) { }
   }
};
