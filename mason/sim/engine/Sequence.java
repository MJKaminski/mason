/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/
package sim.engine;
import java.util.*;
import sim.util.*;



/**
 *
 * Sequence is Steppable which holds an array of Steppables.  When it is stepped,
 * Sequence steps each of its subsidiary Steppables in turn.
 *
 * <p>You provide Sequence
 * with a Collection of Steppables, or with an array of Steppables, via its constructor.
 * It then copies the Collection or array into its own internal array and uses that whenever
 * you step it.
 *
 * <p>You can also modify the Steppables after the fact, in one of three ways.  First, you
 * can provide a new Collection or array to replace the internal array it is presently using, via the
 * method replaceSteppables(...).  Second, you can provide a collection of Steppables to be
 * removed from the internal array, via the methods removeSteppable(...) or removeSteppables(...).
 * Third, you can provide a collection of Steppables to be added to the internal array, via 
 * the methods addSteppable(...) or addSteppables(...).  Sequence delays performing these actions 
 * until step(...) is called on it.  At which time it first replaces the Steppabes with those provided
 * by replaceSteppables(...), then removes any Steppables requested, then finally adds any Steppables
 * requested.  It then steps all the Steppables in the resulting internal array.
 *
 * <p>By default, after removing Steppables from the internal array, Sequence does not guarantee
 * that the remaining Steppables will still be in the same order.  It does this for speed.  If you
 * want to force them to be in the same order, you can call setEnsuresOrder(true).  Note that
 * even if the array has a consistent order internally, subclasses are free to ignore this: for
 * example, RandomSequence and ParallelSequence do not keep the order consistent.
 *
 * <p>Removing Steppables is costly: the Sequence has to hunt through its array to find the ones
 * you've asked to be removed, and that's O(n) per Steppable to remove.  If you are often removing
 * a fair number of Steppables (perhaps more than 5 at a time), Sequence provides a further option
 * which results in O(1) removal: using an internal Set.  The procedure is as follows: all the current
 * Steppables, or the ones to relace them, are maintained in a LinkedHashSet.  The Steppables to 
 * be removed are removed from the Set (O(1) per Steppable).  Steppables to be added are then added
 * to the Set.  Finally the Set is dumped to an array, which is then Stepped.
 *
 * <p>This approach is dramatically faster than the default approach when a large number of Steppables
 * are in the Sequence and at least a moderate number (5 or greater typically) is removed at a time.
 * It has two disadvantages however.   First, it is slower when the number of Steppables is very
 * small, or when the number of Steppables removed is small (less than 5 perhaps).  Second, because
 * a Set is used, the Steppables in the Sequence must be unique: you cannot insert the same Steppable
 * multiple times in the array.  To turn on this option, call setUsesSets(true).
 *
 * @author Mark Coletti
 * @author Sean Luke
 * 
 */
 
public class Sequence implements Steppable
    {
    private static final long serialVersionUID = 1L;

    protected Steppable[] steps;
    protected int size;

    LinkedHashSet stepsHash = null;
    
    Bag toBeRemoved = new Bag();
    Bag toBeAdded = new Bag();
    Steppable[] toReplace = null;
    boolean ensuresOrder = false;

    public Sequence(Steppable[] steps)
        {
        this.steps = (Steppable[])(steps.clone());
        size = steps.length;
        }
        
    public Sequence(Collection collection)
        {
        steps = new Steppable[collection.size()];
        steps = (Steppable[])(collection.toArray(steps));
        }

    public boolean getEnsuresOrder() { return ensuresOrder; }
    public void setEnsuresOrder(boolean val) { ensuresOrder = val; }
    
    public boolean getUsesSets() { return stepsHash != null; }
    public void setUsesSets(boolean val) 
        { 
        if (val && stepsHash == null) 
            {
            stepsHash = new LinkedHashSet();
            for(int i = 0; i < size; i++)
                if (!stepsHash.add(steps[i]))
                    throw new RuntimeException("This Sequence is set up to use Sets, but duplicate Steppables were added to the sequence, which is not permitted in this mode.");
            }
        else if (!val && stepsHash != null)
            {
            stepsHash = null; 
            }
        }

    void loadStepsSet()
        {
        boolean stepsHashChanged = false;
        
        // First, replace the steppables if called for
        if (toReplace != null)
            {
            stepsHashChanged = true;
            stepsHash.clear();
            for(int i = 0; i < toReplace.length; i++)
                if (!stepsHash.add(toReplace[i]))
                    throw new RuntimeException("This Sequence is set up to use Sets, but duplicate Steppables were added to the sequence, which is not permitted in this mode.");
            size = toReplace.length;
            toReplace = null;
            }
    
        // Remove steppables
        int toBeRemovedSize = this.toBeRemoved.size();
        if (toBeRemovedSize > 0)
            {
            stepsHashChanged = true;
            for(int i = 0; i < toBeRemovedSize; i++)
                {
                stepsHash.remove(toBeRemoved.get(i));
                }
            toBeRemoved.clear();
            }
        
        // add in new steppables
        int toBeAddedSize = this.toBeAdded.size();
        if (toBeAddedSize > 0)
            {
            stepsHashChanged = true;
            for(int i = 0; i < toBeAddedSize; i++)
                {
                if (!stepsHash.add(toBeAdded.get(i)))
                    // throw new RuntimeException("This Sequence is set up to use Sets, but duplicate Steppables were added to the sequence, which is not permitted in this mode.");
                    { } // do nohing
                }
            toBeAdded.clear();
            }

        // copy over set
        if (stepsHashChanged)
            {
            if (steps == null)
                steps = new Steppable[stepsHash.size()];
            steps = (Steppable[]) (stepsHash.toArray(steps));
            size = steps.length;
            }
        }
        

    protected void loadSteps()
        {
        if (stepsHash != null)
            {
            loadStepsSet();
            return;
            }
        
        // First, replace the steppables if called for
        if (toReplace != null)
            {
            steps = toReplace;
            size = steps.length;
            toReplace = null;
            }
    
        // Remove steppables
        int toBeRemovedSize = toBeRemoved.size();
        if (toBeRemovedSize > 0)
            {
            boolean ensuresOrder = this.ensuresOrder;
            Steppable[] steps = this.steps;
            Bag toBeRemoved = this.toBeRemoved;
            int stepsSize = this.size;
            
            for(int i = 0; i < stepsSize; i++)
                {
                for(int j = 0; j < toBeRemovedSize; j++)
                    {
                    if (steps[i] == toBeRemoved.get(j))
                        {
                        // remove from steps, possibly nondestructively
                        if (ensuresOrder)
                            if (i < stepsSize - 1)  // I'm not already top
                                System.arraycopy(steps, i+1, steps, i, stepsSize - i -1);
                            else
                                // swap to top
                                steps[i] = steps[stepsSize-1];
                            
                        steps[stepsSize-1] = null;  // let top element GC
                        stepsSize--;
                        i--;
                    
                        // remove from toBeRemoved, always destructively
                        toBeRemoved.remove(j);
                        toBeRemovedSize--;
                        
                        break;  // all done
                        }
                    }
                    
                if (toBeRemovedSize == 0)      // nothing left
                    break;
                }

            //if (!toBeRemoved.isEmpty())  // hmmmm
            //    System.err.println("Some elements not found in removal list for SequenceMark");

            // finish up
            toBeRemoved.clear();
            this.size = stepsSize;
            }


        // add in new steppables
        int toBeAddedSize = this.toBeAdded.size();
        if (toBeAddedSize > 0)
            {
            // extend steppables
            Bag toBeAdded = this.toBeAdded;
            int stepsSize = this.size;
            int newLen = stepsSize + toBeAddedSize;
            if (newLen >= steps.length)
                {
                int newSize = steps.length * 2 + 1;
                if (newSize <= newLen) newSize = newLen;
                Steppable[] newSteppables = new Steppable[newSize];
                System.arraycopy(steps, 0, newSteppables, 0, steps.length);
                this.steps = steps = newSteppables;
                }
            
            // copy in new elements
            if (toBeAddedSize < 20)
                for(int i = 0; i < toBeAddedSize; i++)
                    steps[stepsSize + i] = (Steppable)(toBeAdded.get(i));
            else
                toBeAdded.copyIntoArray(0, steps, stepsSize, toBeAddedSize);


            // finish up
            toBeAdded.clear();            
            this.size = newLen;
            }
        }


    public void replaceSteppables(Collection collection)
        {
        if (toReplace == null)
            toReplace = new Steppable[collection.size()];
        toReplace = (Steppable[])(collection.toArray(toReplace));
        }

    public void replaceSteppables(Steppable[] steppables)
        {
        toReplace = (Steppable[])(steppables.clone());
        }

    public void addSteppable(Steppable steppable)
        {
        toBeAdded.add(steppable);
        }

    public void addSteppables(Steppable[] steppables)
        {
        toBeAdded.addAll(steppables);
        }

    public void removeSteppables(Steppable[] steppables)
        {
        toBeRemoved.addAll(steppables);
        }

    public void addSteppables(Collection steppables)
        {
        toBeAdded.addAll(steppables);
        }

    public void removeSteppables(Collection steppables)
        {
        toBeRemoved.addAll(steppables);
        }

    public void step(SimState state)
        {
        loadSteps();

        int stepsSize = this.size;
        Steppable[] steps = this.steps;
        
        for(int x=0;x<stepsSize;x++)
            {
            if (steps[x]!=null) 
                {
                steps[x].step(state);
                }
            }
        }

    }
