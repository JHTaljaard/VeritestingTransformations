/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.Serializable;
import java.util.*;

/**
 * Main entry-point into the library.
 * <p>
 * Options represents a collection of {@link Option} objects, which
 * describe the possible options for a command-line.
 * <p>
 * It may flexibly parse long and short options, with or without
 * values.  Additionally, it may parse only a portion of a commandline,
 * allowing for flexible multi-stage parsing.
 *
 *
 * @version $Id: Options.java 1754332 2016-07-27 18:47:57Z britter $
 */
public class Options implements Serializable
{
    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /** a map of the options with the character key */
    private final Map<Character, Option> shortOpts = new LinkedHashMap<Character, Option>();

    /** a map of the options with the long key */
    private final Map<Character, Option> longOpts = new LinkedHashMap<Character, Option>();

    /** a map of the required options */
    // N.B. This can contain either a String (addOption) or an OptionGroup (addOptionGroup)
    // TODO this seems wrong
    private final List<Object> requiredOpts = new ArrayList<Object>();

    /** a map of the option groups */
    private final Map<Character, OptionGroup> optionGroups = new LinkedHashMap<Character, OptionGroup>();

    /**
     * Add the specified option group.
     *
     * @param group the OptionGroup that is to be added
     * @return the resulting Options instance
     */
    public Options addOptionGroup(OptionGroup group)
    {
        if (group.isRequired())
        {
            requiredOpts.add(group);
        }

        for (Option option : group.getOptions())
        {
            // an Option cannot be required if it is in an
            // OptionGroup, either the group is required or
            // nothing is required
            option.setRequired(false);
            addOption(option);

            optionGroups.put(option.getKey(), group);
        }

        return this;
    }

    /**
     * Lists the OptionGroups that are members of this Options instance.
     *
     * @return a Collection of OptionGroup instances.
     */
    Collection<OptionGroup> getOptionGroups()
    {
        return new HashSet<OptionGroup>(optionGroups.values());
    }

    /**
     * Add an option that only contains a short name.
     * 
     * <p>
     * The option does not take an argument.
     * </p>
     *
     * @param opt Short single-character name of the option.
     * @param description Self-documenting description
     * @return the resulting Options instance
     * @since 1.3
     */
    public Options addOption(char opt, String description)
    {
        addOption(opt, '\0', 0, description);
        return this;
    }

    /**
     * Add an option that only contains a short-name.
     *
     * <p>
     * It may be specified as requiring an argument.
     * </p>
     *
     * @param opt Short single-character name of the option.
     * @param hasArg flag signally if an argument is required after this option
     * @param description Self-documenting description
     * @return the resulting Options instance
     */
    public Options addOption(char opt, int hasArg, String description)
    {
        addOption(opt, '\0', hasArg, description);
        return this;
    }

    /**
     * Add an option that contains a short-name and a long-name.
     *
     * <p>
     * It may be specified as requiring an argument.
     * </p>
     *
     * @param opt Short single-character name of the option.
     * @param longOpt Long multi-character name of the option.
     * @param hasArg flag signally if an argument is required after this option
     * @param description Self-documenting description
     * @return the resulting Options instance
     */
    public Options addOption(char opt, char longOpt, int hasArg, String description)
    {
        addOption(new Option(opt, longOpt, hasArg, description));
        return this;
    }

    /**
     * Add an option that contains a short-name and a long-name.
     * 
     * <p>
     * The added option is set as required. It may be specified as requiring an argument. This method is a shortcut for:
     * </p>
     *
     * <pre>
     * <code>
     * Options option = new Option(opt, longOpt, hasArg, description);
     * option.setRequired(true);
     * options.add(option);
     * </code>
     * </pre>
     *
     * @param opt Short single-character name of the option.
     * @param longOpt Long multi-character name of the option.
     * @param hasArg flag signally if an argument is required after this option
     * @param description Self-documenting description
     * @return the resulting Options instance
     * @since 1.4
     */
    public Options addRequiredOption(char opt, char longOpt, int hasArg, String description)
    {
        Option option = new Option(opt, longOpt, hasArg, description);
        option.setRequired(true);
        addOption(option);
        return this;
    }

    /**
     * Adds an option instance
     *
     * @param opt the option that is to be added
     * @return the resulting Options instance
     */
    public Options addOption(Option opt)
    {
        char key = opt.getKey();

        // add it to the long option list
        if (opt.hasLongOpt())
        {
            longOpts.put(opt.getLongOpt(), opt);
        }

        // if the option is required add it to the required list
        if (opt.isRequired())
        {
            if (requiredOpts.contains(key))
            {
                requiredOpts.remove(requiredOpts.indexOf(key));
            }
            requiredOpts.add(key);
        }

        shortOpts.put(key, opt);

        return this;
    }

    /**
     * Retrieve a read-only list of options in this set
     *
     * @return read-only Collection of {@link Option} objects in this descriptor
     */
    public Collection<Option> getOptions()
    {
        return Collections.unmodifiableCollection(helpOptions());
    }

    /**
     * Returns the Options for use by the HelpFormatter.
     *
     * @return the List of Options
     */
    List<Option> helpOptions()
    {
        return new ArrayList<Option>(shortOpts.values());
    }

    /**
     * Returns the required options.
     *
     * @return read-only List of required options
     */
    public List getRequiredOptions()
    {
        return Collections.unmodifiableList(requiredOpts);
    }

    /**
     * Retrieve the {@link Option} matching the long or short name specified.
     *
     * <p>
     * The leading hyphens in the name are ignored (up to 2).
     * </p>
     *
     * @param opt short or long name of the {@link Option}
     * @return the option represented by opt
     */
    public Option getOption(char[] opt)
    {
        opt = Util.stripLeadingHyphens(opt);

        if (shortOpts.containsKey(opt[0]))
        {
            return shortOpts.get(opt[0]);
        }

        return longOpts.get(opt[0]);
    }

    /**
     * Returns the options with a long name starting with the name specified.
     * 
     * @param opt the partial name of the option
     * @return the options matching the partial name specified, or an empty list if none matches
     * @since 1.3
     */
    public List<Character> getMatchingOptions(char[] opt)
    {
        opt = Util.stripLeadingHyphens(opt);
        
        List<Character> matchingOpts = new ArrayList<Character>();

        // for a perfect match return the single option only
        if (longOpts.keySet().contains(opt))
        {
            return Collections.singletonList(opt[0]);
        }

        for (char longOpt : longOpts.keySet())
        {
            if (longOpt == opt[0])
            {
                matchingOpts.add(longOpt);
            }
        }
        
        return matchingOpts;
    }

    /**
     * Returns whether the named {@link Option} is a member of this {@link Options}.
     *
     * @param opt short or long name of the {@link Option}
     * @return true if the named {@link Option} is a member of this {@link Options}
     */
    public boolean hasOption(char[] opt)
    {
        opt = Util.stripLeadingHyphens(opt);
        if (opt.length == 0) return false;

        // Vaibhav: My hypothesis is that if opt contains symbolic chars, SPF should either return a symbolic boolean
        // here or branch somewhere inside LinkedHashMap.containsKey(). But, when I run this version of ApacheCLI,
        // I don't see this branch happening, which means that SPF somehow has trouble with branching inside this method.
//        return shortOpts.containsKey(opt[0]) || longOpts.containsKey(opt[0]);
        boolean ret = false;
        Iterator it = shortOpts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Character, Option> pair = (Map.Entry)it.next();
            if (pair.getKey() == opt[0]) ret = true;
        }
        return ret;
    }

    /**
     * Returns whether the named {@link Option} is a member of this {@link Options}.
     *
     * @param opt long name of the {@link Option}
     * @return true if the named {@link Option} is a member of this {@link Options}
     * @since 1.3
     */
    public boolean hasLongOption(String opt)
    {
        char[] opt1 = Util.stripLeadingHyphens(opt.toCharArray());

        return longOpts.containsKey(new String(opt1));
    }

    /**
     * Returns whether the named {@link Option} is a member of this {@link Options}.
     *
     * @param opt short name of the {@link Option}
     * @return true if the named {@link Option} is a member of this {@link Options}
     * @since 1.3
     */
    public boolean hasShortOption(String opt)
    {
        char[] opt1 = Util.stripLeadingHyphens(opt.toCharArray());

        return shortOpts.containsKey(new String(opt1));
    }

    /**
     * Returns the OptionGroup the <code>opt</code> belongs to.
     *
     * @param opt the option whose OptionGroup is being queried.
     * @return the OptionGroup if <code>opt</code> is part of an OptionGroup, otherwise return null
     */
    public OptionGroup getOptionGroup(Option opt)
    {
        return optionGroups.get(opt.getKey());
    }

    /**
     * Dump state, suitable for debugging.
     *
     * @return Stringified form of this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append("[ Options: [ short ");
        buf.append(shortOpts.toString());
        buf.append(" ] [ long ");
        buf.append(longOpts);
        buf.append(" ]");

        return buf.toString();
    }
}
