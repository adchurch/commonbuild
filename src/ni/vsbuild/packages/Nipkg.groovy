package ni.vsbuild.packages

import groovy.json.JsonSlurperClassic

class Nipkg extends AbstractPackage {

   def releaseVersion
   def stagingPath
   def devXmlPath
   
   Nipkg(script, packageInfo, payloadDir) {
      super(script, packageInfo, payloadDir)
      this.releaseVersion = packageInfo.get('release_version')
      this.stagingPath = packageInfo.get('install_destination')
      this.devXmlPath = packageInfo.get('dev_xml_path')
   }

   void buildPackage(lvVersion) {
      def packageInfo = """
         Building package $name from $payloadDir
         Package version: $releaseVersion
         Staging path: $stagingPath
         LabVIEW/VeriStand version: $lvVersion
         Custom Device XML Path: $devXmlPath
         """.stripIndent()
      
      script.cloneCommonbuildConfiguration()
      script.configSetup()

      def versionConfigJsonFile = script.readJSON file: 'configuration.json'
      def convertedVersionConfigJson = new JsonSlurperClassic().parseText(versionConfigJsonFile.toString())
      
      def repo = getComponentParts()['repo']
      def branch = getComponentParts()['branch']

      script.echo "$repo $branch"
      
      def versionConfig = convertedConfigJson.repositories.get('${repo}-${branch}')
      def buildNumber = versionConfig.get('build')
      script.echo "$buildNumber"


      script.buildNipkg(payloadDir, releaseVersion, stagingPath, devXmlPath, lvVersion)
      script.echo packageInfo
   }
}

