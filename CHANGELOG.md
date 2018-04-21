# Change Log

## 2017-07-29
- Updating to work with gatling-stress-v1.2

## 2017-07-29
- Adding Solr Insert/Query Example

## 2017-06-07
- Updated Graph examples to use config files and gatling-dse-stress methods

## 2017-05-14
- Added inclusion of bash scripts to create executable jar on build
  - New executable jar found after build in `build/gatling-dse-sims`
- Updated Simulations and Classes to be compatible with gatling-dse-stress v1.1
- Adopted use of createKeyspace function in Sim actions
- Updated dependencies

## 2017-03-30 
- Renamed appConf to simConf to match correct naming scheme in example files
- Removed unused configuration params for loadGenerator (usersRampStartCnt, usersRampToCnt)
- Inclusion of InsertMembersSimulation
- Moved to used LazyLogging libraries