package net.minecraft.server.dedicated;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

public class DedicatedPlayerList extends ServerConfigurationManager
{
    private File opsList;
    private File whiteList;

    public DedicatedPlayerList(DedicatedServer par1DedicatedServer)
    {
        super(par1DedicatedServer);
        this.opsList = par1DedicatedServer.getFile("ops.txt");
        this.whiteList = par1DedicatedServer.getFile("white-list.txt");
        this.viewDistance = par1DedicatedServer.getIntProperty("view-distance", 10);
        this.maxPlayers = par1DedicatedServer.getIntProperty("max-players", 20);
        this.setWhiteListEnabled(par1DedicatedServer.getBooleanProperty("white-list", false));

        if (!par1DedicatedServer.isSinglePlayer())
        {
            this.getBannedPlayers().setListActive(true);
            this.getBannedIPs().setListActive(true);
        }

        this.getBannedPlayers().loadBanList();
        this.getBannedPlayers().saveToFileWithHeader();
        this.getBannedIPs().loadBanList();
        this.getBannedIPs().saveToFileWithHeader();
        this.loadOpsList();
        this.readWhiteList();
        this.saveOpsList();

        if (!this.whiteList.exists())
        {
            this.saveWhiteList();
        }
    }

    public void setWhiteListEnabled(boolean par1)
    {
        super.setWhiteListEnabled(par1);
        this.getDedicatedServerInstance().setProperty("white-list", Boolean.valueOf(par1));
        this.getDedicatedServerInstance().saveProperties();
    }

    /**
     * This adds a username to the ops list, then saves the op list
     */
    public void addOp(String par1Str)
    {
        super.addOp(par1Str);
        this.saveOpsList();
    }

    /**
     * This removes a username from the ops list, then saves the op list
     */
    public void removeOp(String par1Str)
    {
        super.removeOp(par1Str);
        this.saveOpsList();
    }

    /**
     * Remove the specified player from the whitelist.
     */
    public void removeFromWhitelist(String par1Str)
    {
        super.removeFromWhitelist(par1Str);
        this.saveWhiteList();
    }

    /**
     * Add the specified player to the white list.
     */
    public void addToWhiteList(String par1Str)
    {
        super.addToWhiteList(par1Str);
        this.saveWhiteList();
    }

    /**
     * Either does nothing, or calls readWhiteList.
     */
    public void loadWhiteList()
    {
        this.readWhiteList();
    }

    private void loadOpsList()
    {
        try
        {
            this.getOps().clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.opsList));
            String s = "";

            while ((s = bufferedreader.readLine()) != null)
            {
                this.getOps().add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        }
        catch (Exception exception)
        {
            this.getDedicatedServerInstance().getLogAgent().logWarning("Failed to load operators list: " + exception);
        }
    }

    private void saveOpsList()
    {
        try
        {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.opsList, false));
            Iterator iterator = this.getOps().iterator();

            while (iterator.hasNext())
            {
                String s = (String)iterator.next();
                printwriter.println(s);
            }

            printwriter.close();
        }
        catch (Exception exception)
        {
            this.getDedicatedServerInstance().getLogAgent().logWarning("Failed to save operators list: " + exception);
        }
    }

    private void readWhiteList()
    {
        try
        {
            this.getWhiteListedPlayers().clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.whiteList));
            String s = "";

            while ((s = bufferedreader.readLine()) != null)
            {
                this.getWhiteListedPlayers().add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        }
        catch (Exception exception)
        {
            this.getDedicatedServerInstance().getLogAgent().logWarning("Failed to load white-list: " + exception);
        }
    }

    private void saveWhiteList()
    {
        try
        {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.whiteList, false));
            Iterator iterator = this.getWhiteListedPlayers().iterator();

            while (iterator.hasNext())
            {
                String s = (String)iterator.next();
                printwriter.println(s);
            }

            printwriter.close();
        }
        catch (Exception exception)
        {
            this.getDedicatedServerInstance().getLogAgent().logWarning("Failed to save white-list: " + exception);
        }
    }

    /**
     * Determine if the player is allowed to connect based on current server settings.
     */
    public boolean isAllowedToLogin(String par1Str)
    {
        par1Str = par1Str.trim().toLowerCase();
        return !this.isWhiteListEnabled() || this.areCommandsAllowed(par1Str) || this.getWhiteListedPlayers().contains(par1Str);
    }

    public DedicatedServer getDedicatedServerInstance()
    {
        return (DedicatedServer)super.getServerInstance();
    }

    public MinecraftServer getServerInstance()
    {
        return this.getDedicatedServerInstance();
    }
}
