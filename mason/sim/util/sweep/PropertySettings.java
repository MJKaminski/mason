package sim.util.sweep;

import ec.util.*;
import sim.util.*;

import javax.swing.*;
import java.util.List;

/**
 * Created by dfreelan on 12/19/17.
 */
public class PropertySettings 
    {
    public Properties p;
    public int index;
    public double min;
    public double max;
    public boolean average;
    public boolean minValue;
    public boolean maxValue;
    public double value;
    public int steps=2;
    public boolean everyStep = false;
    public int nStep = 0; 
    public boolean amSet = false;
    public boolean amDependent = false;
    
    public PropertySettings(Properties p, int index)
        {
        this.p = p;
        this.index = index;
        }
    
    public static ParameterDatabase convertToDatabase(ListModel propertySettings, int simSteps, String path, int repeats, int threads)
        {
        StringBuilder min = new StringBuilder("");
        StringBuilder max= new StringBuilder("");
        StringBuilder avg= new StringBuilder("");
        StringBuilder independent= new StringBuilder("");
        StringBuilder dependent= new StringBuilder("");
        StringBuilder steps= new StringBuilder("");
        StringBuilder everyStep= new StringBuilder("");
        StringBuilder recordMin= new StringBuilder("");
        StringBuilder recordMax= new StringBuilder("");
        StringBuilder nStep = new StringBuilder("");
        ParameterDatabase pd = new ParameterDatabase();
        for(int i = 0; i<propertySettings.getSize(); i++)
            {
            PropertySettings param = (PropertySettings) propertySettings.getElementAt(i);
            if(param.amSet) 
                {
                addParameter(min, max, avg,recordMin, recordMax, independent, dependent, steps, everyStep, nStep,  param);
                }
            }
        pd.set(new Parameter("min"),min.toString());
        pd.set(new Parameter("max"),max.toString());
        pd.set(new Parameter("avg"),avg.toString());
        pd.set(new Parameter("recordMin"),recordMin.toString());
        pd.set(new Parameter("recordMax"),recordMax.toString());
        pd.set(new Parameter("independent"),independent.toString());
        pd.set(new Parameter("dependent"),dependent.toString());
        pd.set(new Parameter("steps"),steps.toString());
        pd.set(new Parameter("everyStep"),everyStep.toString());
        pd.set(new Parameter("nStep"), String.valueOf(nStep));
        pd.set(new Parameter("simulationSteps"),simSteps+"");
        pd.set(new Parameter("out"),path);
        pd.set(new Parameter("numRepeats"),repeats+"");
        pd.set(new Parameter("threads"),threads+"");
        System.err.println("steps is " + steps.toString().substring(0,steps.toString().length()));
        System.err.println("min is " + min.toString().substring(0,min.toString().length()));
        System.err.println("max is " + max.toString().substring(0,max.toString().length()));
        System.err.println("independent names"  + independent.toString().substring(0,independent.toString().length()));

        return pd;

        }

    public static void addParameter(StringBuilder min, StringBuilder max, StringBuilder avg, StringBuilder recordMax, StringBuilder recordMin, StringBuilder independent, StringBuilder dependent, StringBuilder steps, StringBuilder everyStep, StringBuilder nStep, PropertySettings param) 
        {
        if(param.amDependent)
            {
            dependent.append(param.getName());
            dependent.append(",");
            everyStep.append(param.everyStep);
            everyStep.append(",");
            nStep.append(param.nStep);
            nStep.append(",");
            avg.append(param.average);
            avg.append(",");
            recordMin.append(param.minValue);
            recordMin.append(",");
            recordMax.append(param.maxValue);
            recordMax.append(",");
            }
        else
            {
            independent.append(param.getName());
            independent.append(",");
            min.append(param.min);
            min.append(",");
            max.append(param.max);
            max.append(",");
            steps.append(param.steps);
            steps.append(",");
            }
        }

    public String getName()
        {
        return p.getName(index);
        }
    
    public String toString()
        {
        StringBuilder builder = new StringBuilder("<html>"+p.getName(index));
        if(!amSet)
            return builder.toString() + "</html>";

        if(!amDependent) 
            {
            builder.append("<font color='red'> min=" + min + " max=" + max);
            builder.append(" steps=" + steps);
            }
        else
            {
            builder.append("<font color='blue'> (DEPENDENT) ");
            if (average)
                builder.append(" average=" + average);
            if (minValue)
                builder.append(" minValue=" + minValue);
            if (maxValue)
                builder.append(" maxValue=" + maxValue);
            if (everyStep)
                builder.append(" everyStep=" + everyStep);
            }

        return builder.toString()+ "</font></html>";
        }
    
    public void set(boolean everyStep)
        {
        //   this.value = value;
        this.everyStep = everyStep;
        amSet=true;
        amDependent=true;

        }
    
    public void set(double min, double max, int steps, boolean average, boolean minValue, boolean maxValue)
        {
        this.min = min;
        this.max = max;
        this.steps = steps;
        this.average = average;
        this.minValue=minValue;
        this.maxValue = maxValue;
        amSet = true;
        amDependent = false;
        }
    
    public void unset()
        {
        average = false;
        minValue = false;
        maxValue = false;
        amSet = false;
        }
    }
