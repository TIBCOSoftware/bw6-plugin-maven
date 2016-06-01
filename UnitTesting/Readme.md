# Assertions plugin for TIBCO ActiveMatrix BusinessWorks™ 6

### To add plugin to existing Installation:

For DesignTime (Business Studio):
- Stop Business Studio if open.
- Create dropins folder inside $BW_HOME/studio/4.0/eclipse.
- Add jars from Install/design folder (design and model) in the dropins folder
- Edit $BWHOME/studio/4.0/eclipse/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info in a text editor.
- Find dropins keyword, scroll to end of line - change false to true.
- Restart Business Studio
- The plugin is now available for design time.


For Runtime:

- Copy both model and runtime bundles from Install/runtime folder into system/shared folder
- While launching debugger(Launch Configuration) make sure both bundles are selected
- For standalone deployment (outside studio) no additional steps are required


### Activity Description and Modes
The plugin adds a single activity - AssertNodeEquals.

The activity has two modes : Primitive and Activity
The Primitive mode allows you to combine multiple output elements from upstream activities and assert their values.

The activity mode allows you to pick an activity in the same process and provide a 'golden' output. The output of the activity you selected will now be shown in the Input tab of the AssertNodeEquals activity.

You can also provide an XML file as the golden output by selecting Golden Output from File on the Advance Tab.

### Usage
The AssertNodeEquals activity does not provide an output. It throws an ActivityFault if assertion fails with fault details. This can be caught using the Catch All Fault Handler. If assertion passes, no output is generated.
