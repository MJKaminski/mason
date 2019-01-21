/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package sim.util.sweep;
import java.awt.*;
import javax.swing.JScrollPane;

import ec.util.*;
import sim.display.*;
import sim.display.Console;
import sim.engine.*;
import sim.portrayal.*;
import sim.portrayal.inspector.*;
import sim.portrayal.simple.*;
import sim.util.*;
import sim.util.gui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class ParameterSweepGUI extends JPanel
    {
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
    GUIState state;
    JPanel outerParameterPanel;
    sim.util.Properties properties;

    int numMaxSteps = 10000;
    int numRepeats = 1;
    long seed;
    String filePath;
    int numThreads=1;
    
    JButton clear;
    JCheckBox minValue = new JCheckBox("record min value");
    JCheckBox maxValue = new JCheckBox("record max value");
    JCheckBox avgValue = new JCheckBox("record avg value");
    JCheckBox everyStep = new JCheckBox("record every n steps", false);
    JTextField nStep = new JTextField("0");
    JLabel nStepsLabel = new JLabel("n=");

    PropertyField maxStepsField;
    PropertyField repeatsField;
    PropertyField seedField;
    // JFileChooser fileField;
    PropertyField threadsField;

    /**
     * The current index of the topmost element
     */
    int start = 0;
    /**
     * The number of items presently in the propertyList
     */
    int count = 0;
    int currentIndex = 0;
    String listName;  // used internally
    JLabel numElements = null;
    Box startField = null;
    Thread runThread;
    public JPanel propPanel = new JPanel();
    Thread sweeperThread =null;
    ParameterSweep sweeper = null;

    JList<PropertySettings> propList = null;
    private ArrayList<Component> currentComponents = new ArrayList<Component>();

    DefaultListModel<PropertySettings> propertySettingsList;
    public GUIState getGUIState() 
        {
        return state;
        }

    public ParameterSweepGUI(sim.util.Properties properties, final GUIState state, String name) 
        {
        setLayout(new BorderLayout());
        add(split, BorderLayout.CENTER);
                
        split.setResizeWeight(.45);
        //split.setDividerLocation(60);

        propPanel.setLayout(new BorderLayout());

        numElements = new JLabel();
        this.state = state;
        listName = name;
        this.properties = properties;

        propList = new JList<PropertySettings>();
        propertySettingsList = new DefaultListModel<PropertySettings>();

        for (int i = 0; i < properties.numProperties(); i++) 
            {
            if(!properties.isHidden(i) && (properties.getType(i)==Integer.TYPE
                    ||properties.getType(i)==Double.TYPE || properties.getType(i)==Float.TYPE
                    || properties.getType(i)==Boolean.TYPE || properties.getType(i)==Long.TYPE))
                propertySettingsList.addElement(new PropertySettings(properties, i));
            }
        propList.setModel(propertySettingsList);
        propList.setVisibleRowCount(10);


        generateProperties();
        // making sure that recording each step in the sweep is enabled by default, and that users cannot set a number of steps to skip without first unchecking "everystep"
        nStep.setEditable(false);

        ListSelectionListener listener = new ListSelectionListener() 
            {
            public void valueChanged(ListSelectionEvent e) 
                {
                outerParameterPanel.removeAll();
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                currentIndex = lsm.getMaxSelectionIndex();
                propList.setSelectedIndex(currentIndex);

                PropertySettings currentProp = propList.getSelectedValue();
                if(currentProp!=null) 
                    {
                    updateParameters(currentProp);
                    }
                }
            };
        
        avgValue.addActionListener(new ActionListener() 
            {
            @Override
            public void actionPerformed(ActionEvent e) 
                {
                propList.getSelectedValue().average=avgValue.isSelected();
                propList.revalidate();
                propList.repaint();
                }
            });
        
        minValue.addActionListener(new ActionListener() 
            {
            @Override
            public void actionPerformed(ActionEvent e) 
                {
                propList.getSelectedValue().minValue=minValue.isSelected();
                propList.revalidate();
                propList.repaint();
                }
            });
        
        maxValue.addActionListener(new ActionListener() 
            {
            @Override
            public void actionPerformed(ActionEvent e) 
                {
                propList.getSelectedValue().maxValue=maxValue.isSelected();
                propList.revalidate();
                propList.repaint();
                }
            });
        
        everyStep.addActionListener(new ActionListener() 
            {
            @Override
            public void actionPerformed(ActionEvent e) 
                {
                propList.getSelectedValue().everyStep=everyStep.isSelected();
                nStep.setEditable(true);
                propList.revalidate();
                propList.repaint();
                }
            });
        nStep.addActionListener(new ActionListener() 
            {
            @Override
            public void actionPerformed(ActionEvent e) 
                {
                propList.getSelectedValue().nStep=Integer.parseInt(nStep.getText());
                propList.revalidate();
                propList.repaint();
                }
            });
        
        propList.getSelectionModel().addListSelectionListener(listener);
        //show the first item in the list's settings by default

        propList.getSelectionModel().getMaxSelectionIndex();

        split.setTopComponent(new JScrollPane(propList));

        String align = BorderLayout.AFTER_LAST_LINE;

        final JButton run = new JButton(sim.display.Console.iconFor("NotPlaying.png"));
        run.setPressedIcon(sim.display.Console.iconFor("Playing.png"));
        final JButton stop = new JButton(sim.display.Console.iconFor("NotStopped.png"));
        stop.setPressedIcon(sim.display.Console.iconFor("Stopped.png"));
        
        run.addActionListener(new ActionListener() 
            {
            public void actionPerformed(ActionEvent e) 
                {
                try 
                    {
                    if(!isValidConfiguration())
                        {
                        JOptionPane.showMessageDialog(null,"You need to have both an independent and a dependent variable set to run");
                        return;
                        }
                    
                    FileDialog dialog = new FileDialog((Frame)null, "Save Results to File...", FileDialog.SAVE);
                    dialog.setFilenameFilter(new FilenameFilter()
                        {
                        public boolean accept(File dir, String name)
                            {
                            return name.endsWith(".csv");
                            }
                        });
                        
                    dialog.setVisible(true);
                    String filename = dialog.getFile();
                    String directory = dialog.getDirectory();
                    if (filename == null)
                        return;
                        
                    try 
                        { 
                        filePath = new File(new File(directory), filename).getCanonicalPath();
                        }
                    catch (IOException ex)
                        {
                        ex.printStackTrace();
                        return;
                        }
                    
                    run.setIcon(sim.display.Console.iconFor("Playing.png"));
                    stop.setIcon(sim.display.Console.iconFor("NotStopped.png"));
                    final ParameterDatabase pd = PropertySettings.convertToDatabase(propList.getModel(),numMaxSteps,filePath,Integer.parseInt(repeatsField.getValue()),Integer.parseInt(threadsField.getValue()));
                    pd.set(new Parameter("app"),state.state.getClass().getName());
                    pd.set(new Parameter("simulationSteps"), "" + numMaxSteps);
                    sweeper = new ParameterSweep(pd);
                    sweeperThread = new Thread(
                        new Runnable()
                            {
                            @Override
                            public void run() 
                                {

                                try 
                                    {
                                    sweeper.runSweepFromParamDatabase();
                                    run.setIcon(sim.display.Console.iconFor("NotPlaying.png"));
                                    }
                                catch(Exception e)
                                    {
                                    e.printStackTrace();
                                    }
                                }
                            });
                    sweeperThread.start();

                    } catch (Exception b) 
                    {
                    b.printStackTrace();
                    }
                //runner.run();
                }
            });
        run.setBorderPainted(false);
        run.setContentAreaFilled(false);
        run.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        stop.addActionListener(new ActionListener() 
            {
            @Override
            public void actionPerformed(ActionEvent e) 
                {
                run.setIcon(sim.display.Console.iconFor("NotPlaying.png"));
                stop.setIcon(sim.display.Console.iconFor("Stopped.png"));
                if(sweeper!=null) 
                    {
                    sweeper.stop = true;

                    }
                }
            });
        stop.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        stop.setBorderPainted(false);
        stop.setContentAreaFilled(false);

        clear = new JButton("Reset Params");
        clear.addActionListener(new ActionListener() 
            {
            @Override
            public void actionPerformed(ActionEvent e) 
                {
                for (int i = 0; i < propList.getModel().getSize(); i++) 
                    {
                    PropertySettings p = propList.getModel().getElementAt(i);
                    p.unset();
                    PropertySettings currentProp = propList.getSelectedValue();
                    if(currentProp!=null) 
                        {
                        updateParameters(currentProp);
                        }
                    }
                repaint();
                }
            });

        maxStepsField = new PropertyField("10000")
            {
            public String newValue(String value)
                {
                try
                    {
                    numMaxSteps = Integer.parseInt(value);
                    if (numMaxSteps < 1) numMaxSteps = 1;
                    }
                catch (NumberFormatException ex)
                    {
                    }
                return "" + numMaxSteps;
                }
            };

        maxStepsField.getField().setColumns(7);  // make enough space
        maxStepsField.setMaximumSize(maxStepsField.getField().getPreferredSize());
        maxStepsField.setPreferredSize(maxStepsField.getField().getPreferredSize());
        

        seedField = new PropertyField("" + (int)state.state.seed() )
            {
            public String newValue(String value)
                {
                try
                    {
                    seed = Long.parseLong(value);
                    }
                catch (NumberFormatException ex)
                    {
                    }
                return "" + seed;
                }
            };

        seedField.getField().setColumns(10);  // make enough space
        seedField.setMaximumSize(seedField.getField().getPreferredSize());
        seedField.setPreferredSize(seedField.getField().getPreferredSize());
        
        JLabel maxStepsLabel = new JLabel("Steps: ");

        JLabel seedLabel = new JLabel("Seed: ");

        JLabel repeatLabel = new JLabel("Repeats: ");

        JLabel threadLabel = new JLabel("Threads: ");

        repeatsField = new PropertyField("1")
            {
            public String newValue(String value)
                {
                try
                    {
                    numRepeats = Integer.parseInt(value);
                    if (numRepeats < 1) numRepeats = 1;
                    }
                catch (NumberFormatException ex)
                    {
                    }
                return "" + numRepeats;
                }
            };

        repeatsField.getField().setColumns(4);  // make enough space
        repeatsField.setMaximumSize(repeatsField.getField().getPreferredSize());
        repeatsField.setPreferredSize(repeatsField.getField().getPreferredSize());
        
        threadsField = new PropertyField("1")
            {
            public String newValue(String value)
                {
                try
                    {
                    numThreads = Integer.parseInt(value);
                    if (numThreads < 1) numThreads = 1;
                    }
                catch (NumberFormatException ex)
                    {
                    }
                return "" + numThreads;
                }
            };
        threadsField.getField().setColumns(4);  // make enough space
        threadsField.setMaximumSize(threadsField.getField().getPreferredSize());
        threadsField.setPreferredSize(threadsField.getField().getPreferredSize());
                
        outerParameterPanel = new JPanel();
        outerParameterPanel.setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane(outerParameterPanel);
        split.setBottomComponent(scroll);

        LabelledList globalSettings = new LabelledList("Sweep Settings");
        globalSettings.addLabelled("Num Trials: ", repeatsField);
        globalSettings.addLabelled("Num Threads: ", threadsField);
        globalSettings.addLabelled("Max Steps: ", maxStepsField);
        globalSettings.addLabelled("Initial Seed: ", seedField);

        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(globalSettings, BorderLayout.CENTER);
                
        Box firstLine = new Box(BoxLayout.X_AXIS);
        firstLine.add(run);
        firstLine.add(stop);
        firstLine.add(firstLine.createGlue());
        p.add(firstLine, BorderLayout.SOUTH);

        add(p, BorderLayout.SOUTH);

        propList.setSelectedIndex(0);
        }

    public boolean isValidConfiguration() 
        {
        boolean isValidConfiguration = true;
        boolean hasIndependent = false;
        boolean hasDependent = false;
        for(int i = 0; i<propList.getModel().getSize(); i++)
            {
            PropertySettings prop = propList.getModel().getElementAt(i);
            if(prop.amSet && prop.amDependent)
                {
                hasIndependent = true;
                }else if(prop.amSet && !prop.amDependent)
                {
                hasDependent = true;
                }
            }
        if(!hasIndependent || !hasDependent)
            {
            isValidConfiguration = false;
            }
        return isValidConfiguration;
        }


    /* Creates a JPopupMenu that possibly includes "View" to
       view the object instead of using the ViewButton.  If not, returns null. */
    JPopupMenu makePreliminaryPopup(final int index) 
        {
        if (properties.isComposite(index)) 
            {
            JPopupMenu popup = new JPopupMenu();
            JMenuItem menu = new JMenuItem("View");
            menu.setEnabled(true);
            menu.addActionListener(new ActionListener() 
                {
                public void actionPerformed(ActionEvent e) 
                    {
                    sim.util.Properties props = properties;
                    final Inspector inspector = Inspector.getInspector(props.getValue(index), ParameterSweepGUI.this.state, null);
                    Stoppable stopper = null;
                    try 
                        {
                        stopper = ParameterSweepGUI.this.state.scheduleRepeatingImmediatelyAfter(inspector.getUpdateSteppable());
                        } catch (IllegalArgumentException ee)  // this can happen if the simulation is over, so nothing further can be scheduled (notably the Stopper)
                        {
                        // make a dummy stopper
                        stopper = new Stoppable() 
                            {
                            public void stop() 
                                {
                                }
                            };
                        }
                    stopper = inspector.reviseStopper(stopper);
                    ParameterSweepGUI.this.state.controller.registerInspector(inspector, stopper);
                    JFrame frame = inspector.createFrame(stopper);
                    frame.setVisible(true);


                    }
                });
            popup.add(menu);
            return popup;
            } else return null;
        }

    void updateParameters(PropertySettings currentProp)
        {
        updatePropertySettingsUI(new int[] {currentProp.index,currentProp.index,currentProp.index});
        if (currentProp.amSet)
            {
            if (currentProp.amDependent)
                {
                dependentRadio.setSelected(true);
                avgValue.setSelected(currentProp.average);
                minValue.setSelected(currentProp.minValue);
                maxValue.setSelected(currentProp.maxValue);
                everyStep.setSelected(currentProp.everyStep);
                nStep.setText("" + currentProp.nStep);
                }
            else
                {
                independentRadio.setSelected(true);
                }
            }
        else    // neither
            {
            neitherRadio.setSelected(true);
            }
        }

    PropertyField makePropertyField(final int index, final PropertySettings settings, final String name) 
        {
        Class type = properties.getType(index);
        final sim.util.Properties props = properties;            // see UNUSUAL BUG note below
        //System.err.println("trying to make property field with name " +  name);
        //System.err.println("its index is " + index + " and its type is" + properties.getType(index));
        //System.err.println("is it hidden? " + properties.isHidden(index));
        if(!name.equals("steps")) {

            PropertyField field = new PropertyField(
                null,
                properties.betterToString(properties.getValue(index)),
                properties.isReadWrite(index),
                properties.getDomain(index),
                    (properties.isComposite(index) ?
                    //PropertyField.SHOW_VIEWBUTTON :
                    PropertyField.SHOW_TEXTFIELD :
                        (type == Boolean.TYPE || type == Boolean.class ?
                        PropertyField.SHOW_CHECKBOX :
                            (properties.getDomain(index) == null ? PropertyField.SHOW_TEXTFIELD :
                            (properties.getDomain(index) instanceof Interval) ?
                            PropertyField.SHOW_SLIDER : PropertyField.SHOW_LIST)))) {
                // The return value should be the value you want the display to show instead.
                public String newValue(final String newValue) 
                    {
                    if(propList.getSelectedValue().index!=index)
                        {
                        return newValue;
                        }
                    synchronized (ParameterSweepGUI.this.state.state.schedule) 
                        {
                        // try to set the value
                        if (props.setValue(index, newValue) == null)
                            Toolkit.getDefaultToolkit().beep();
                        // refresh the controller -- if it exists yet
                        if (ParameterSweepGUI.this.state.controller != null)
                            ParameterSweepGUI.this.state.controller.refresh();


                        if (name.equals("min")) 
                            {
                            //System.err.println("changing the min of " + props.getName(index)  + " from " + settings.min + "  to " + Double.parseDouble(props.betterToString(props.getValue(index))));
                            settings.min = Double.parseDouble(props.betterToString(props.getValue(index)));
                            } else if (name.equals("max")) 
                            {
                            //System.err.println("changing the max of " + props.getName(index)  + " from " + settings.max + "  to " + Double.parseDouble(props.betterToString(props.getValue(index))));
                            settings.max = Double.parseDouble(props.betterToString(props.getValue(index)));
                            }
                        java.util.List<PropertySettings> list = propList.getSelectedValuesList();
                        /*    if (list.size() > 0) 
                              {
                              PropertySettings active = list.get(list.size() - 1);
                              if (dependentRadio.isSelected()) 
                              {
                              try 
                              {
                              active.set(everyStep.isSelected());
                              } catch (NumberFormatException e2) 
                              {
                              }
                              } 
                              else 
                              {
                              try 
                              {
                              active.set(settings.min, settings.max, settings.steps, settings.average, settings.minValue, settings.maxValue);
                              } catch (NumberFormatException e2) 
                              {
                              e2.printStackTrace();
                              }
                              }
                              }*/

                        repaint();
                        revalidate();

                        propList.repaint();
                        propList.revalidate();
                        // set text to the new value
                        return props.betterToString(props.getValue(index));
                        }

                    }
                };
            //System.err.println(("am i at leats set?") + propList.getSelectedValue().amSet);
            if(!propList.getSelectedValue().amDependent && propList.getSelectedValue().amSet) 
                {
                if (name.equals("min")) 
                    {
                    field.setValue(propList.getSelectedValue().min + "");
                    //System.err.println("SETTING THE MIN TO " + propList.getSelectedValue().min);

                    } else if (name.equals("max")) 
                    {
                    field.setValue(propList.getSelectedValue().max + "");
                    }
                }
            propList.repaint();
            propList.revalidate();

            return field;
            }
        else
            {
            String steps = settings==null ? "3" : ""+settings.steps;
            PropertyField custom =  new PropertyField(steps) 
                {
                // The return value should be the value you want the display to show instead.
                public String newValue(final String newValue) 
                    {
                    synchronized (ParameterSweepGUI.this.state.state.schedule) 
                        {
                        // try to set the value

                        // refresh the controller -- if it exists yet
                        if (ParameterSweepGUI.this.state.controller != null)
                            ParameterSweepGUI.this.state.controller.refresh();


                        settings.steps = (int)Double.parseDouble(newValue);
                        java.util.List<PropertySettings> list = propList.getSelectedValuesList();
                        PropertySettings active = list.get(list.size() - 1);
                        try 
                            {
                            if(settings.steps<1)
                                {
                                settings.steps=1;
                                }
                            active.set(settings.min, settings.max, settings.steps, settings.average, settings.minValue, settings.maxValue);
                            } 
                        catch (NumberFormatException e2) 
                            {
                            }

                        repaint();
                        revalidate();

                        // set text to the new value
                        propList.revalidate();
                        propList.repaint();
                        return ""+newValue;
                        }

                    }
                };
            return custom;

            }
        }

    JRadioButton independentRadio = new JRadioButton("Independent", true);
    JRadioButton dependentRadio = new JRadioButton("Dependent");
    JRadioButton neitherRadio = new JRadioButton("Neither");

    int index[];

    void updatePropertySettingsUI(int[] index) 
        {
        this.index = index;

        final Box propertyList = new Box(BoxLayout.Y_AXIS);

        //... Create a button group and add the buttons.
        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(independentRadio);
        bgroup.add(dependentRadio);
        bgroup.add(neitherRadio);
        neitherRadio.setSelected(true);

        propertyList.add(independentRadio);
        //don't allow to be independent if it's read-only
        if(!properties.isReadWrite(propList.getSelectedValue().index)) 
            {
            independentRadio.setEnabled(false);
            }
        else
            {
            independentRadio.setEnabled(true);
            if(propList.getSelectedValue().amSet && propList.getSelectedValue().amDependent)
                {
                dependentRadio.setSelected(true);
                } else if(!propList.getSelectedValue().amSet)
                {
                neitherRadio.setSelected(true);
                }
            }

        propertyList.add(dependentRadio);
        propertyList.add(neitherRadio);
        propertyList.add(clear);

        JPanel independentPanel = new JPanel();
        independentPanel.setLayout(new BorderLayout());
        LabelledList list = new LabelledList();
        final PropertyField min = makePropertyField(index[0], propList.getSelectedValue(),"min");
        final PropertyField max = makePropertyField(index[0], propList.getSelectedValue(),"max");
        final PropertyField steps = makePropertyField(index[0], propList.getSelectedValue(),"steps");
        propList.getSelectedValue().min=Double.parseDouble(min.getValue());
        propList.getSelectedValue().max=Double.parseDouble(max.getValue());
        propList.getSelectedValue().steps=Integer.parseInt(steps.getValue());
        list.addLabelled("Min ", makePropertyField(index[0], propList.getSelectedValue(), "min"));
        list.addLabelled("Max ", makePropertyField(index[1], propList.getSelectedValue(), "max"));
        list.addLabelled("Steps ", makePropertyField(index[2], propList.getSelectedValue(), "steps"));
        independentPanel.add(list, BorderLayout.NORTH);

        JPanel dependentPanel = new JPanel();
        dependentPanel.setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        box.add(minValue);
        box.add(maxValue);
        box.add(avgValue);
        box.add(everyStep);
        Box hBox = new Box(BoxLayout.X_AXIS);
        hBox.add(nStepsLabel);
        hBox.add(nStep);
        box.add(hBox);
        dependentPanel.add(box, BorderLayout.NORTH);

        JPanel neitherPanel = new JPanel();

        final JPanel subPanel = new JPanel();
        final CardLayout cardLayout = new CardLayout();
        subPanel.setLayout(cardLayout);
        subPanel.add(dependentPanel, "dependent");
        subPanel.add(independentPanel, "independent");
        subPanel.add(neitherPanel, "neither");
        cardLayout.show(subPanel, "neither");
        if(propList.getSelectedValue().amSet)
            {
            if(propList.getSelectedValue().amDependent)
                {
                cardLayout.show(subPanel, "dependent");
                }
            else
                {
                cardLayout.show(subPanel, "independent");
                }
            }
        dependentRadio.addActionListener(new ActionListener() 
            {
            @Override
            public void actionPerformed(ActionEvent e) 
                {

                propList.getSelectedValue().amDependent=true;
                propList.getSelectedValue().amSet=true;

                PropertySettings currentProp = propList.getSelectedValue();

                avgValue.setSelected(currentProp.average);
                minValue.setSelected(currentProp.minValue);
                maxValue.setSelected(currentProp.maxValue);
                everyStep.setSelected(currentProp.everyStep);
                nStep.setText("" + currentProp.nStep);


                cardLayout.show(subPanel, "dependent");

                propList.revalidate();
                propList.repaint();
                }
            });
        neitherRadio.addActionListener(new ActionListener() 
            {
            @Override
            public void actionPerformed(ActionEvent e) 
                {

                propList.getSelectedValue().unset();

                cardLayout.show(subPanel, "neither");
                propList.revalidate();
                propList.repaint();
                }
            });
        independentRadio.addActionListener(new ActionListener() 
            {
            @Override
            public void actionPerformed(ActionEvent e) 
                {

                propList.getSelectedValue().amDependent=false;
                propList.getSelectedValue().amSet=true;



                independentRadio.setSelected(true);

                cardLayout.show(subPanel, "independent");
                ////System.err.println("SETTING VALUE TO " + propList.getSelectedValue().min);
                min.setValue(propList.getSelectedValue().min+"");
                max.setValue(propList.getSelectedValue().max+"");
                steps.setValue(propList.getSelectedValue().steps+"");
                propList.revalidate();
                propList.repaint();
                propertyList.repaint();
                propertyList.revalidate();
                }
            });

        outerParameterPanel.removeAll();
        outerParameterPanel.add(propertyList, BorderLayout.WEST);
        outerParameterPanel.add(subPanel, BorderLayout.CENTER);

        outerParameterPanel.revalidate();
        outerParameterPanel.repaint();
        //System.err.println("TRYING TO REPAINT");
        propPanel.repaint();
        propList.revalidate();
        propList.repaint();
        //propertySettingsList.rev
        Utilities.doEnsuredRepaint(propList);
        propPanel.revalidate();
        propPanel.repaint();
        propList.repaint();
        propList.revalidate();

        //propList.setModel(propertySettingsList);
        }

    void generateProperties() 
        {
        // propertyList = new LabelledList(listName);
        //members = new PropertyField[properties.numProperties()];

        for (int i = 0; i < properties.numProperties(); i++) 
            {
            if (!properties.isHidden(i))  // don't show if the user asked that it be hidden
                {
                JLabel label = new JLabel(properties.getName(i) + " ");
                JToggleButton toggle = PropertyInspector.getPopupMenu(properties, i, state, makePreliminaryPopup(i));
                //members[i] = makePropertyField(i,propList.getSelectedValue(),properties.getName(i));
                //propertyList.add(label, members[i]);
                }
            //else members[i] = null;
            }

        this.start = start;
        }
    }
