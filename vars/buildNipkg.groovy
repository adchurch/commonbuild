def call(payloadDir, buildNumber, stagingPath, devXmlPath, lvVersion) {
   
   def controlFields = readProperties file: "control"
   def basePackageName = "${controlFields.get('Package')}"
   def nipmAppPath = "C:\\Program Files\\National Instruments\\NI Package Manager\\nipkg.exe"
   def controlFileText = readFile "control" // Read nipkg control file 
   def devXmlText = readFile devXmlPath
   def devXml = new XmlSlurper().parseText(devXmlText)
   def baseVersion = devXml.Version.text()
   def nipkgVersion = "${baseVersion}.${buildNumber}"
   echo "nipkg Version: $nipkgVersion"

   // Replace {version} with current VeriStand version being built.
   def newControlFileText = controlFileText.replaceAll("\\{veristand_version\\}", "${lvVersion}")
   finalControlFileText = newControlFileText.replaceAll("\\{nipkg_version\\}", "${nipkgVersion}")
   def newStagingPath = stagingPath.replaceAll("\\{veristand_version\\}", "${lvVersion}")
   def packageName = basePackageName.replaceAll("\\{veristand_version\\}", "${lvVersion}")
   echo newStagingPath

   echo "Building ${packageName} with control file attributes:"
   echo finalControlFileText

   // Copy package payload to nipkg staging directory. 
//   bat "(robocopy \"${payloadDir}\" \"nipkg\\${packageName}\\data\\${newStagingPath}\" /MIR /NFL /NDL /NJH /NJS /nc /ns /np) ^& exit 0"

   writeFile file: "nipkg\\${packageName}\\debian-binary", text: "2.0"      
//   writeFile file: "nipkg\\${packageName}\\control\\control", text: finalControlFileText

   // Build nipkg using NI Package Manager CLI pack command. 
//   bat "\"${nipmAppPath}\" pack \"nipkg\\${packageName}\" \"${payloadDir}\"" 

}

