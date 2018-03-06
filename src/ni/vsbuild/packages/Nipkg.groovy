package ni.vsbuild.packages

import groovy.json.JsonSlurperClassic

class Nipkg extends AbstractPackage {

   def stagingPath
   def devXmlPath
   
   Nipkg(script, packageInfo, payloadDir) {
      super(script, packageInfo, payloadDir)
      this.stagingPath = packageInfo.get('install_destination')
      this.devXmlPath = packageInfo.get('dev_xml_path')
   }

   void buildPackage(lvVersion) {
      def packageInfo = """
         Building package $name from $payloadDir
         Staging path: $stagingPath
         LabVIEW/VeriStand version: $lvVersion
         Custom Device XML Path: $devXmlPath
         """.stripIndent()
      script.echo packageInfo
      
      script.cloneCommonbuildConfiguration()
      script.configSetup()


      def repo = script.getComponentParts()['repo']
      def branch = script.getComponentParts()['branch']
      def componentID = repo+'-'+branch

      script.echo "Getting build version number for ${componentID}."
      def componentConfigJsonFile = script.readJSON file: 'configuration.json'
      def componentConfigStringMap = new JsonSlurperClassic().parseText(componentConfigJsonFile.toString())
      def componentConfig = componentConfigStringMap.repositories.get(componentID)
      def buildNumber = componentConfig.get('build') as Integer
      buildNumber = buildNumber + 1
      def commitMessage = "updating ${componentID} to build number ${buildNumber}."
      componentConfig << [build:buildNumber]

      script.configUpdate(componentID, componentConfigStringMap)
      def baseVersion = script.getDeviceVersion(devXmlPath)
      script.buildNipkg(payloadDir, baseVersion, buildNumber, stagingPath, lvVersion)
      script.configPush(commitMessage) //Push updated build number to commonbuild-configuration repository.
      
   }
}

