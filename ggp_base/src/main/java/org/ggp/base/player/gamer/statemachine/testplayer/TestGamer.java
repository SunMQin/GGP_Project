//package org.ggp.base.player.gamer.statemachine.testplayer;
//
//import javafx.util.Pair;
//import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
//import org.ggp.base.player.gamer.statemachine.sample.SampleGamer;
//import org.ggp.base.util.statemachine.MachineState;
//import org.ggp.base.util.statemachine.Move;
//import org.ggp.base.util.statemachine.Role;
//import org.ggp.base.util.statemachine.StateMachine;
//import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
//import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
//import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
//
//import java.util.*;
//import java.util.concurrent.ThreadLocalRandom;
//
//public class TestGamer extends SampleGamer {
//    private Boolean isFirst = true;
//    private Pair<Integer, Integer> center = null;
//
//    private Role me = null;
//    private Role opponent = null;
//    @Override
//    public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
//        StateMachine theMachine = getStateMachine();
//
//        //获取角色
//        me = getRole();
//        for (Role i:
//             theMachine.getRoleIndices().keySet()) {
//            if(!i.equals(me)){
//                opponent = i;
//                break;
//            }
//        }
//        //获取先后手
//        this.isFirst = (theMachine.getRoleIndices().get(me) == 0);
//
//        //获取中心点位置
//        List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
//        int max = 0;
//        for (Move i:
//             moves) {
//            String move_gdl = i.toString();
//            List<String> tmp = Arrays.asList(move_gdl.split(" "));
//            Integer x = Integer.parseInt(tmp.get(2));
//            Integer y = Integer.parseInt(tmp.get(3));
//
//            Integer max_x_y = Math.max(x, y);
//            if(max < max_x_y) {
//                max = max_x_y;
//            }
//        }
//
//        if(max % 2 == 1) {
//            center = new Pair<>(max / 2 + 1, max / 2 + 1);
//        }
//
//        super.stateMachineMetaGame(timeout);
//    }
//
//    @Override
//    public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
//    {
//        //获取游戏状态机
//        StateMachine theMachine = getStateMachine();
//        long start = System.currentTimeMillis();
//        long finishBy = timeout - 3000;
//
//        //获取当前状态所有可行的动作
//        List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
////        List<Move> tmp_moves = new ArrayList<Move>();
//        Move selection = moves.get(0);
//
//        //判断可行的动作数量是否大于1 小于1直接就是他了
//        if(moves.size() > 1) {
//            //----------启发式------------------可能得花费一点时间 不能停
//            if(isFirst) {
//                //放中间
//                for (Move m:
//                     moves) {
//                    List<String> tmp = Arrays.asList(m.toString().split(" "));
//                    if(tmp.get(2).equals(center.getKey().toString()) && tmp.get(3).equals(center.getValue().toString())){
//                        long stop = System.currentTimeMillis();
//                        notifyObservers(new GamerSelectedMoveEvent(moves, m, stop - start));
//                        return m;
//                    }
//                }
//                isFirst = false;
//            }
//            //如果存在一步获胜，则选取这一步
//            //防止对方下一步能赢
//            {
//                moves = new ArrayList<Move>(moves);
//                Map<Move, List<MachineState>> move_map = theMachine.getNextStates(getCurrentState(), getRole());
//                for (Move i :
//                        move_map.keySet()) {
//                    for (MachineState ms :
//                            move_map.get(i)) {
//                        if (theMachine.isTerminal(ms)) {
//                            if (theMachine.getGoal(ms, getRole()) == 100) {
//                                long stop = System.currentTimeMillis();
//                                notifyObservers(new GamerSelectedMoveEvent(moves, i, stop - start));
//                                return i;
//                            }
//                        }
//                        boolean forcedLoss = false;
//                        for (List<Move> jointMove : theMachine.getLegalJointMoves(ms)) {
//                            MachineState nextNextState = theMachine.getNextState(ms, jointMove);
//                            if (theMachine.isTerminal(nextNextState)) {
//                                if (theMachine.getGoal(nextNextState, getRole()) == 0) {
//                                    forcedLoss = true;
//                                    break;
//                                }
//                            }
//                        }
//                        if (forcedLoss) {
//                            moves.remove(i);
//                            break;
//                        }
//                    }
//                }
//            }
//
////            if(!isFirst) {
////
////            }
//
//            if (moves.size() > 1) {
//                try {
//                    //----------蒙特卡洛树搜索------------------
//                    //创建根结点
//                    TreeNode Root = new TreeNode(moves.size(),);
//                    int loop_i = 0;
//                    //只要还在截止时间内
//                    while (System.currentTimeMillis() <= finishBy) {
//                        loop_i++;
//                        int score;
//
//                        //tmp节点一开始指向根结点
//                        TreeNode tmp = Root;
//
//                        //获取游戏当前局面状态
//                        MachineState state = getCurrentState();
//
//                        //创建反向传播列表 用于最后反向传播是更新对应的w_k值
//                        List<TreeNode> backList = new ArrayList<>();
//
//                        //加入的节点n_k值加一
//                        tmp.setN_k(tmp.getN_k() + 1);
//                        backList.add(tmp);
//
//                        //只要满足选举的条件——该节点的孩子数等于他可有的合法孩子节点数 即所有孩子都至少走过一次
//                        while (tmp.getChild().size() == tmp.getLegalMoveNum()) {
//                            //select
//                            //选举一个最合适的孩子节点
//                            tmp = tmp.checkBestChild();
//
//                            //把该节点加进去 n_k值加一
//                            tmp.setN_k(tmp.getN_k() + 1);
//                            backList.add(tmp);
//
//                            //状态转换
//                            state = theMachine.getNextState(state, theMachine.getRandomJointMove(state, getRole(), tmp.getMove()));
//                            if (theMachine.isTerminal(state))
//                                break;
//                        }
//                        //判断是否为终态
//                        if (!theMachine.isTerminal(state)) {
//                            //expand
//                            //扩展 判断是哪种节点要扩展
//                            TreeNode expandNode = null;
//                            Boolean thisMax = !tmp.getMax();
//
//                            boolean hasNode = false;
//
//                            //从当前状态获取自己的所有合法动作
//                            if (tmp != Root)
//                                moves = theMachine.getLegalMoves(state, getRole());
//
//                            //随机选一个
//                            Move moveUnderConsideration = moves.get(ThreadLocalRandom.current().nextInt(moves.size()));
//
//                            //判断这一个点是不是已经被创建了 从tmp的child里找
//                            for (TreeNode i :
//                                    tmp.getChild()) {
//                                //如果找到一样的就直接模拟
//                                if (i.getMove().equals(moveUnderConsideration)) {
//                                    expandNode = i;
//                                    hasNode = true;
//                                    break;
//                                }
//                            }
//                            if (!hasNode) {
//                                //创建扩展节点
//                                expandNode = new TreeNode(moveUnderConsideration, moves.size(), thisMax);
//                                //把子节点加进父节点child中
//                                tmp.getChild().add(expandNode);
//                            }
//
//                            //把孩子节点加进反向传播列表中
//                            backList.add(expandNode);
//                            expandNode.setN_k(expandNode.getN_k() + 1);
//
//                            //simulation
//                            score = performDepthChargeFromMove(state, moveUnderConsideration);
//                        } else {
//                            score = theMachine.getGoal(state, getRole());
//                        }
//                        //back 反向传播回溯结果
//                        for (TreeNode i :
//                                backList) {
//                            i.setW_k(i.getW_k() + score / 100.0);
//                        }
//                    }
//                    //时间不够 选择根结点的最好子节点
//                    selection = Root.checkBestChild().getMove();
//                    System.out.println("testplayer num of test:" + loop_i);
////                    for (TreeNode i :
////                            Root.getChild()) {
////                        System.out.println(i.getMove() + " : " + i.getUCT(Root.getN_k()));
////                    }
//                    System.out.println(selection);
//                }
//                catch (NoSuchElementException e) {
//                    moves = theMachine.getLegalMoves(getCurrentState(), getRole());
//                    selection = moves.get(ThreadLocalRandom.current().nextInt(moves.size()));
//                    e.printStackTrace();
//                }
//            }
//            else if(moves.size() == 1){
//                selection = moves.get(0);
//            }
//            else {
//                moves = theMachine.getLegalMoves(getCurrentState(), getRole());
//                selection = moves.get(ThreadLocalRandom.current().nextInt(moves.size()));
//            }
//        }
//
//        long stop = System.currentTimeMillis();
//
//        notifyObservers(new GamerSelectedMoveEvent(theMachine.getLegalMoves(getCurrentState(), getRole()), selection, stop - start));
//        return selection;
//    }
//
//    private int[] depth = new int[1];
//    //传入当前状态和我要走的动作 得到随机模拟到终局的分数
//    private int performDepthChargeFromMove(MachineState theState, Move myMove) {
//        StateMachine theMachine = getStateMachine();
//        try {
//            MachineState finalState = theMachine.performDepthCharge(theMachine.getRandomNextState(theState, getRole(), myMove), depth);
//            return theMachine.getGoal(finalState, getRole());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
//}
