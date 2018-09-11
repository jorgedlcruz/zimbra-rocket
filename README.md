# Zimbra Rocket
In this Repository you will find a simple Zimlet to connect Zimbra Collaboration with Rocket Chat, and make an amazing integration with both inside the Zimbra Web Client, it will look like:

![Zimbra Rocket](https://raw.githubusercontent.com/Zimbra-Community/zimbra-rocket/master/img/zimbra-rocket-ui.png)

# Instructions
## How to install Rocket Chat
On this github we will not cover the installation of Rocket Chat, as it's perfectly explained here:
* https://rocket.chat/docs/installation/minimum-requirements
* https://www.digitalocean.com/community/tutorials/how-to-install-configure-and-deploy-rocket-chat-on-ubuntu-14-04
* https://www.howtoforge.com/tutorial/how-to-install-rocket-chat-with-nginx-on-ubuntu-16-04/

Rocket Chat it's also designed to be deployed on Docker or Ubuntu Snap, which is really great:
* https://rocket.chat/docs/installation/docker-containers/
* https://rocket.chat/docs/installation/manual-installation/ubuntu/snaps/

## This Zimlet supports optional auto-login to Rocket using the Rocket API
This Zimlet can automatically log your users on to Rocket chat and even automatically create new users on Rocket chat. That way you only need to maintain the user accounts on Zimbra (full integration). Or you can maintain your Rocket accounts via LDAP or manually, but still log them on automatically (logon-only integration). Or you can just deploy only the Zimlet and let the user decide on the authentication (basic integration).
1. **Full integration**
For this you need to set-up the Java server extension copy it from https://github.com/Zimbra-Community/zimbra-rocket/releases to /opt/zimbra/lib/ext/rocket/extension.jar then create a text file /opt/zimbra/lib/ext/rocket/config.properties with the contents:

        adminuser=adminUsername
        adminpassword=adminPassword
        rocketurl=https://rocket.example.org
        loginurl=https://mail.example.org

This adminuser and password you should have created when you first installed Rocket. The loginurl is the place where we point users to that have not yet authenticated. This can be your SSO login page or the Zimbra login page. Don't forget `zmmailboxdctl restart`.

You must also configure Rocket chat like so:
![Zimbra Rocket](https://raw.githubusercontent.com/Zimbra-Community/zimbra-rocket/master/img/zimbra-rocket-iframe.png)
Be careful, as you can easily lock yourself out if something does not work. If you want more details check: https://github.com/Zimbra-Community/zimbra-rocket/wiki/Debugging

Also enable the full iframe integration like so:
![Zimbra Rocket](https://raw.githubusercontent.com/Zimbra-Community/zimbra-rocket/master/img/zimbra-rocket-iframe2.png?1)

You must also configure and deploy the Zimlet:
Get a com_zimbra_rocket.zip (from Github releases) and as Zimbra user:

      zmzimletctl deploy com_zimbra_rocket.zip
      
To configure the rocketurl in the Zimlet

      zmzimletctl getConfigTemplate /opt/zimbra/zimlets/com_zimbra_rocket > /tmp/config_template.xml.tmp
      
Edit the /tmp/config_template.xml.tmp file according to your needs. Import the new configuration file by the running following command:

      zmzimletctl configure /tmp/config_template.xml.tmp

2. **Logon-only integration**
Follow the same steps as under `Full integration` except when configuring the Zimlet set `createRocketAccount` to `false`. You may also want to configure Rocket to use Zimbra LDAP. See steps below.

3. **Basic integration**
You must configure and deploy the Zimlet:
Get a com_zimbra_rocket.zip (from Github releases) and as Zimbra user:


      zmzimletctl deploy com_zimbra_rocket.zip
      
Configure the `rocketurl` in the Zimlet and set `createRocketAccount` to `false` 

      zmzimletctl getConfigTemplate /opt/zimbra/zimlets/com_zimbra_rocket > /tmp/config_template.xml.tmp
      
Edit the /tmp/config_template.xml.tmp file according to your needs. Import the new configuration file by the running following command:

      zmzimletctl configure /tmp/config_template.xml.tmp

## Clean up authentication tokens
Their appears to be an issue in meteor (the platform on which Rocket is build) that results in authentication tokens not being purged. This is a performance and security issue, as one user record can have thousands of valid tokens slowing down the db. To fix this configure a clean-up cron.

Create a file /usr/local/sbin/rocket-token-purge with contents:

      #!/bin/bash
      cd /snap/rocketchat-server/*/
      ./bin/mongo parties --eval 'db.users.update({}, { $set: { "services.resume.loginTokens": []}},{multi: true})'
      
run `chmod +rx /usr/local/sbin/rocket-token-purge` and put it in cron `crontab -e` like so:

      2 3 1 * * /usr/local/sbin/rocket-token-purge
      
This will log people out of the Rocket.Chat app on their phone, so they need to re-enter their credentials.

See also: https://github.com/RocketChat/Rocket.Chat/issues/6738

## How to Configure Rocket Chat with Zimbra LDAP (optional)
Configure Rocket Chat with the Zimbra LDAP is really easy, and you must follow the next steps:

Go to Administration and then LDAP, and enable the LDAP option, mark the option as shown, change the LDAP hostname with your Zimbra one (the Rocket and the Zimbra must have Network visibility)
![Zimbra Rocket](https://raw.githubusercontent.com/Zimbra-Community/zimbra-rocket/master/img/rocket-001.png)

On the *Domain Base* add the next, change it with your domain: *ou=people,dc=zimbra,dc=io*
On the *Domain Search User* select the next: *uid=zimbra,cn=admins,cn=zimbra*
On the *Domain Search Password* add your LDAP Password, you can retrieve on the Zimbra server with *zmlocalconfig -s zimbra_ldap_password*
![Zimbra Rocket](https://raw.githubusercontent.com/Zimbra-Community/zimbra-rocket/master/img/rocket-002.png)

On the next fields, please fill them as shown:
![Zimbra Rocket](https://raw.githubusercontent.com/Zimbra-Community/zimbra-rocket/master/img/rocket-003.png)

On the latest fields, fill them as shown and finally click on test, and Sync users, and Save
![Zimbra Rocket](https://raw.githubusercontent.com/Zimbra-Community/zimbra-rocket/master/img/rocket-004.png)

# Special Bonus, Zimbra Talk Integration and Zimbra Rocket
Zimbra Rocket doesn't end into an amazing look-a-like Slack experience, and you can even integrate your Zimbra Talk to make video calls between users, one-to-one and one-to-many, follow the next steps to enable it:

Go to Administration > Videoconference and add your Zimbra Talk FQDN, and the Zimbra Talk Chrome ID for the Share Screen Feature:
![Zimbra Rocket](https://raw.githubusercontent.com/Zimbra-Community/zimbra-rocket/master/img/rocket-009.png)

Then the users will be able to call each other, or to groups by using the camera icon:
![Zimbra Rocket](https://raw.githubusercontent.com/Zimbra-Community/zimbra-rocket/master/img/rocket-010.png)

# Troubleshooting
https://github.com/Zimbra-Community/zimbra-rocket/wiki/Debugging

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
