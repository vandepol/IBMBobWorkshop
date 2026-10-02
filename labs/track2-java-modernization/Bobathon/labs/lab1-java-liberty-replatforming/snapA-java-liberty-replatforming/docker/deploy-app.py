import sys
warFile = "/work/config/simple-pharmacy.war"
appName = "simple-pharmacy"
print "Installing application: " + appName
AdminApp.install(warFile, ["-appname", appName, "-contextroot", "/pharmacy", "-MapWebModToVH", [[".*", ".*", "default_host"]]])
AdminConfig.save()
print "Application installed successfully"

# Made with Bob
