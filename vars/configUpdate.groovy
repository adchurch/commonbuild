def call(configurationJson) {

   // Write configuration to JSON file and then convert it back to TOML. 
   writeFile file: 'configuration.json', text: configurationJson
   bat "commonbuild\\resources\\saveConfig.bat"

}

