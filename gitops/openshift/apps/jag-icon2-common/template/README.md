## Templates to create openshift components related to jag-icon2-common api deployment

### Command to execute template
1) Login to OC using login command
2) Run below command in each env. namespace dev/test/prod/tools
   ``oc process -f jag-icon2-common.yaml --param-file=jag-icon2-common.env | oc apply -f -``
