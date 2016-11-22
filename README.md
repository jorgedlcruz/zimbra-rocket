# Zimbra Rocket
In this Repository you will find a simple Zimlet to connect Zimbra Collaboration with Rocket Chat, and make an amazing integration with both inside the Zimbra Web Client, it will look like:

![Zimbra Rocket](https://github.com/jorgedlcruz/zimbra-zimlets/raw/master/img/zimbra-rocket-ui.png)

# Instructions
## How to install Rocket Chat
On this github we will not cover the installation of Rocket Chat, as it's perfectly explained here:
* https://rocket.chat/docs/installation/minimum-requirements
* https://www.digitalocean.com/community/tutorials/how-to-install-configure-and-deploy-rocket-chat-on-ubuntu-14-04
* https://www.howtoforge.com/tutorial/how-to-install-rocket-chat-with-nginx-on-ubuntu-16-04/

Rocket Chat it's also designed to be deployed on Docker or Ubuntu Snap, which is really great:
* https://rocket.chat/docs/installation/docker-containers/
* https://rocket.chat/docs/installation/manual-installation/ubuntu/snaps/

## How to Configure Rocket Chat with Zimbra LDAP
Configure Rocket Chat with the Zimbra LDAP is really easy, and you must follow the next steps:

Go to Administration and then LDAP, and enable the LDAP option, mark the option as shown, change the LDAP hostname with your Zimbra one (the Rocket and the Zimbra must have Network visibility)
![Zimbra Rocket](https://github.com/jorgedlcruz/zimbra-zimlets/raw/master/img/rocket-001.png)

On the *Domain Base* add the next, change it with your domain: *ou=people,dc=zimbra,dc=io*
On the *Domain Search User* select the next: *uid=zimbra,cn=admins,cn=zimbra*
On the *Domain Search Password* add your LDAP Password, you can retrieve on the Zimbra server with *zmlocalconfig -s zimbra_ldap_password*
![Zimbra Rocket](https://github.com/jorgedlcruz/zimbra-zimlets/raw/master/img/rocket-002.png)

On the next fields, please fill them as shown:
![Zimbra Rocket](https://github.com/jorgedlcruz/zimbra-zimlets/raw/master/img/rocket-003.png)

On the latest fields, fill them as shown and finally click on test, and Sync users, and Save
![Zimbra Rocket](https://github.com/jorgedlcruz/zimbra-zimlets/raw/master/img/rocket-004.png)

# How to Download and Deploy the Zimbra Rocket Zimlet
Please download the .zip file, or clone this repo, you must edit the file called *com_zimbra_rocket.js* with your details like:
* Name for the Tab on the next:
```bash
         this.ZimbraRocketTab = this.createApp("Rocket", "", "Rocket");
```
And the Rocket Chat URL (https://YOUR-ROCHET-CHAT\) and the size of the iFrame if needed:
```php
         app.setContent('<div style="position: fixed; left:0; width:100%; height:89%; border:0px;"><iframe id="ZimbraRocketFrame" style="z-index:2; left:0; width:100%; height:100%; border:0px;" src=\"https://YOUR-ROCHET-CHAT\"></div>');
```
Then create a .zip file with the files, *important* not a zip with a folder and inside the files, just files, in Linux or Mac if you are in the Folder with the files run:
```bash
zip -r com_zimbra_rocket.zip .
adding: .DS_Store (deflated 97%)
adding: com_zimbra_rocket.css (deflated 16%)
adding: com_zimbra_rocket.js (deflated 63%)
adding: com_zimbra_rocket.xml (deflated 55%)
adding: README.md (deflated 32%)
adding: rocket.png (stored 0%)
```

Then, you are ready to add the .zip to your Admin Console as usual:
![Zimbra Rocket](https://github.com/jorgedlcruz/zimbra-zimlets/raw/master/img/rocket-005.png)

If everything went okay, you will see the next message
![Zimbra Rocket](https://github.com/jorgedlcruz/zimbra-zimlets/raw/master/img/rocket-006.png)

Then you need to enable the Zimlet in the Class of Service you want:
![Zimbra Rocket](https://github.com/jorgedlcruz/zimbra-zimlets/raw/master/img/rocket-007.png)

And finally, the users will see it on their Tabs, and they need to introduce the credentials again, this will be fixed soon:
![Zimbra Rocket](https://github.com/jorgedlcruz/zimbra-zimlets/raw/master/img/rocket-008.png)

# Special Bonus, Zimbra Talk Integration and Zimbra Rocket
Zimbra Rocket doesn't end into an amazing look-a-like Slack experience, and you can even integrate your Zimbra Talk to make video calls between users, one-to-one and one-to-many, follow the next steps to enable it:

Go to Administration > Videoconference and add your Zimbra Talk FQDN, and the Zimbra Talk Chrome ID for the Share Screen Feature:
![Zimbra Rocket](https://github.com/jorgedlcruz/zimbra-zimlets/raw/master/img/rocket-009.png)

Then the users will be able to call each other, or to groups by using the camera icon:
![Zimbra Rocket](https://github.com/jorgedlcruz/zimbra-zimlets/raw/master/img/rocket-010.png)

## ToDo
- [ ] Add a way to store the LDAP user and Password within the Zimlet
- [ ] Make the vertical-right features bar from Rocket on all Zimbra Web Client, so keep it there static

========================================================================
### License
The MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

