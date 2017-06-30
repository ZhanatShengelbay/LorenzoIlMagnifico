package it.polimi.ingsw.pc22.adapters;

import it.polimi.ingsw.pc22.connection.GameMatch;
import it.polimi.ingsw.pc22.connection.GameServer;
import it.polimi.ingsw.pc22.connection.User;
import it.polimi.ingsw.pc22.gamebox.*;
import it.polimi.ingsw.pc22.messages.CommunicationMessage;
import it.polimi.ingsw.pc22.messages.ErrorMessage;
import it.polimi.ingsw.pc22.messages.Message;
import it.polimi.ingsw.pc22.messages.TimerMessage;
import it.polimi.ingsw.pc22.player.Player;
import it.polimi.ingsw.pc22.utils.CouncilPrivilege;
import it.polimi.ingsw.pc22.utils.UserLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fandroid95 on 30/05/2017.
 */
public abstract class IOAdapter
{
    private Long timeout;

    public abstract void endConnection(Player player) throws IOException;

    public abstract void  printMessage(Message message);

    public abstract String getMessage();

    public Long getTimeout()
    {
        return timeout;
    }

    public void setTimeout(Long timeout)
    {
        this.timeout = timeout;
    }

    public FamilyMember askFamiliarMemberForBonus(Player player)
    {
        List<FamilyMember> familyMembers =
                player.getFamilyMembers();

        Long maxTimeStamp = System.currentTimeMillis() + timeout;

        while(System.currentTimeMillis() < maxTimeStamp)
        {
 		   StringBuilder sb = new StringBuilder("Choose the familiar member for the bonus:\n");

 		   //TODO SISTEMARE STA COSA; BISOGNA CHIAMARE UNA FUNZIONE STATICA ESTERNA!!
		   for (FamilyMember f : familyMembers)
			   sb.append(f.toString());

		   //this.printMessage(sb.toString());

		    String choice = this.getMessage();

		    if (choice == null)
		        continue;

		    ColorsEnum color = ColorsEnum.getColorFromString(choice);

		    if (color == null)
		        continue;

		    FamilyMember member = player.getFamilyMemberByColor(color);

		    if (member == null)
		        continue;

		    return member;

        }

        printMessage(new TimerMessage("Timeout Azione terminato"));

        return null;
    }

    public int askExcommunication() {

        Long maxTimeStamp = System.currentTimeMillis() + timeout;

        while(System.currentTimeMillis() < maxTimeStamp)
        {
            /*this.printMessage("Choose an action:\n"
                    + "1) Show your support to the Church (you'll lose all yours faith points & you'll gain victory points)\n"
                    + "2) Take the excommunication (you won't lose your faith points)");
                    */

            String value = getMessage();

            if (value == null)
                continue;

            Integer choice;

            try
            {
                choice = Integer.parseInt(value);
            }
            catch (NumberFormatException e)
            {
                this.printMessage(new ErrorMessage("ERROR! You must enter a valid input"));

                continue;
            }

            if (choice !=1 || choice !=2)
                continue;

            return choice;
        }

        printMessage(new TimerMessage("Timeout Azione terminato"));

        return -1;

    }

    public void printWinnerNameToSingleUser(String playerName)
    {
        this.printMessage(new CommunicationMessage("The winner is: " + playerName));

    }

    public Player authenticate(String authentication) throws IOException
    {
        Pattern loginPattern = Pattern.compile("(^(\\w+) (\\w+) (L|S|l|s)$)");

        Matcher matcher = loginPattern.matcher(authentication);

        if (!matcher.find())
        {
            printMessage(new ErrorMessage("Invalid INPUT"));

            return null;
        }

        String[] login = authentication.split(" ");

        User user = null;

        if ("L".equalsIgnoreCase(login[2]))
        {
            user = loginUser(login[0],login[1]);
        }

        if ("S".equalsIgnoreCase(login[2]))
        {
            user = signUp(login[0], login[1]);

            updateJson();
        }

        if (user == null) return null;

        Player player = new Player();

        player.setName(user.getUsername());

        return player;
    }

    public boolean gameHandling(String choice) throws IOException
    {
        String[] userString = choice.split(":");

        System.out.println(userString[0] + " " + userString[1]);

        Map<String, User> usersMap = GameServer.getUsersMap();

        User user = usersMap.get(userString[0]);

        if (user == null)
        {
            printMessage(new ErrorMessage("Invalid INPUT no user found"));

            return false;
        }

        String playerName = user.getUsername();

        Player player = new Player();

        player.setName(playerName);

        Pattern gameMatcher = Pattern.compile("(^(\\w+) (C|c|J|j|R|r)$)");

        Matcher matcher = gameMatcher.matcher(userString[1]);

        if (!matcher.find())
        {
            printMessage(new ErrorMessage("Invalid INPUT"));

            return false;
        }

        String[] matchStrings = userString[1].split(" ");

        Map<String, GameMatch> gameMatchMap = GameServer.getGameMatchMap();

        if("C".equalsIgnoreCase(matchStrings[1]))
        {
            return createNewGame
                    (gameMatchMap, matchStrings[0], player);
        }

        if("J".equalsIgnoreCase(matchStrings[1]))
        {
            return addToExistingGame
                    (gameMatchMap, matchStrings[0], player);
        }

        if ("R".equalsIgnoreCase(matchStrings[1]))
        {
            if (firstMatchFree(gameMatchMap).equals(null))
            {
                printMessage(new ErrorMessage("No matches available."));
            }

            return addToExistingGame(gameMatchMap, this.firstMatchFree(gameMatchMap), player);
        }

        printMessage(new ErrorMessage("Non-valid input. Please retry... "));

       return false;
    }

    private synchronized String firstMatchFree(Map<String, GameMatch> gameMatchMap)
    {
        for (Map.Entry<String, GameMatch> entry : gameMatchMap.entrySet())
        {
            if (entry.getValue().getPlayerCounter() < 5)
            {
                return entry.getKey();
            }

        }

        return null;
    }

    private synchronized boolean createNewGame
        (Map<String, GameMatch> gameMatchMap, String gameName, Player player)
            throws IOException
    {
        boolean existingGameMatch = gameMatchMap.containsKey(gameName);

        if (existingGameMatch)
        {
            printMessage(new ErrorMessage("A game match with the specified name already exists."));

            return false;
        }

        startNewGameMatch(gameName, player);

        printMessage(new CommunicationMessage("Player: " + player.getName() + " created GameMatch - " + gameName));

        return true;
    }

    private synchronized boolean addToExistingGame
        (Map<String, GameMatch> gameMatchMap, String gameName, Player player)
            throws IOException
    {
        boolean existingGameMatch = gameMatchMap.containsKey(gameName);

        if (!existingGameMatch)
        {
            printMessage(new ErrorMessage("Game match not found."));

            return false;
        }

        GameMatch gameMatch = gameMatchMap.get(gameName);

        //TODO FARE IN MODO CHE SI POSSA RILOGGARE

        if (gameMatch.getStarted())
        {
            printMessage(new ErrorMessage("Game already started"));

            return false;
        }

        if (gameMatch.getMaxPlayersNumber() == gameMatch.getPlayerCounter())
        {
            printMessage(new ErrorMessage("Game is full"));

            return false;
        }

        player.setAdapter(this);

        List<Player> players = gameMatch.getPlayers();

        players.add(player);

        gameMatch.setPlayers(players);

        int counter = gameMatch.getPlayerCounter() + 1;

        gameMatch.setPlayerCounter(counter);

        if (gameMatch.getPlayerCounter() == 2)
            new Thread(gameMatch).start();

        printMessage(new CommunicationMessage("Player: " + player.getName() + " joined GameMatch - " + gameName));

        return true;
    }


    private synchronized User loginUser(String username, String password)
    {
        Map<String, User> usersMap = GameServer.getUsersMap();

        User user = usersMap.get(username);

        if (user == null)
        {
            printMessage(new ErrorMessage("User not found"));

            return null;
        }

        if (!user.getPassword().equals(password))
        {
            printMessage(new ErrorMessage("Wrong password"));

            return null;
        }

        if (user.isLogged())
        {
            printMessage(new ErrorMessage("Invalid login"));

            return null;
        }

        user.setLogged(true);

        return user;
    }

    private synchronized User signUp(String username, String password) throws IOException
    {
        Map<String, User> usersMap = GameServer.getUsersMap();

        User user = usersMap.get(username);

        if (user != null)
        {
            printMessage(new ErrorMessage("Invalid login"));

            return null;
        }

        user = new User(username, password, true);

        usersMap.put(user.getUsername(), user);

        return user;
    }

    //TODO FAR SI CHE I VALORI VENGANO GESTITI DAL PARSER JSON
    //TODO2 DISTINGUERE IL TIMEOUT DELL'AVVIO PARTITA DAL TIMEOUT DELL'AZIONE (ce ne vogliono 2 distinti)
    private void startNewGameMatch(String gameName, Player player)
    {
        GameMatch gameMatch = new GameMatch(30000L, 4);

        Map<String, GameMatch> gameMatchMap = GameServer.getGameMatchMap();

        gameMatchMap.put(gameName, gameMatch);

        player.setAdapter(this);

        List<Player> players = new ArrayList<>();

        players.add(player);

        gameMatch.setPlayers(players);

        int counter = gameMatch.getPlayerCounter() + 1;

        gameMatch.setPlayerCounter(counter);
    }

    synchronized private void updateJson() throws IOException
    {
        Map<String, User> usersMap = GameServer.getUsersMap();

        UserLoader.refreshJson(usersMap);
    }

}