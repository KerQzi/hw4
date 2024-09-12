import java.util.Random;

public class hw4 {
    public static int bossHealth = 1000;
    public static int bossDamage = 50;
    public static String bossDefence;
    public static int[] heroesHealth = {270, 280, 250, 280, 400, 180, 300, 250};
    public static int[] heroesDamage = {20, 15, 10, 0, 5, 10, 0, 20};
    public static String[] heroesAttackType = {"Physical", "Magical", "Kinetic", "Medical", "Golem", "Lucky", "Witcher", "Thor"};
    public static int roundNumber = 0;
    public static boolean bossStunned = false;

    public static void main(String[] args) {
        printStatistics();
        while (!isGameOver()) {
            playRound();
        }
    }

    public static boolean isGameOver() {
        if (bossHealth <= 0) {
            System.out.println("Heroes won!!!");
            return true;
        }
        boolean allHeroesDead = true;
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0) {
                allHeroesDead = false;
                break;
            }
        }
        if (allHeroesDead) {
            System.out.println("Boss won!!!");
            return true;
        }
        return false;
    }

    public static void playRound() {
        roundNumber++;
        chooseBossDefence();
        witcherAbility();
        if (!bossStunned) { // если босс застанен он не пропускает атаку
            bossAttack();
        } else {
            bossStunned = false; // возвращаем значение
        }
        heroesAttack();
        medicHealAbility();
        printStatistics();
    }

    public static void chooseBossDefence() {
        Random random = new Random();
        int randomIndex = random.nextInt(heroesAttackType.length - 5); // оставить три основных типа атаки
        bossDefence = heroesAttackType[randomIndex];
    }

    public static void bossAttack() {
        int golemIndex = 4;
        int luckyIndex = 5;
        int damageToGolem = bossDamage / 5;

        Random random = new Random();

        if (heroesHealth[golemIndex] > 0) {
            // Урон другим героям с учетом способность голема
            for (int i = 0; i < heroesHealth.length; i++) {
                if (heroesHealth[i] > 0 && i != golemIndex && i != luckyIndex) {
                    heroesHealth[i] = Math.max(heroesHealth[i] - (bossDamage - damageToGolem), 0);
                    heroesHealth[golemIndex] = Math.max(heroesHealth[golemIndex] - damageToGolem, 0); // Голем принимает 1/5 от общего урона
                } else if (i == golemIndex){ // обычный урон по голему
                    heroesHealth[golemIndex] = Math.max(heroesHealth[golemIndex] - bossDamage, 0);
                }
            }
        } else { // Если Голем мертв
            for (int i = 0; i < heroesHealth.length; i++) {
                if (heroesHealth[i] > 0 && i != luckyIndex) {
                    heroesHealth[i] = Math.max(heroesHealth[i] - bossDamage, 0);
                }
            }
        }

        if (heroesHealth[luckyIndex] > 0 && random.nextBoolean()){ // уклонение
            System.out.println("Lucky dodged the attack!");
        } else {
            if (heroesHealth[luckyIndex] > 0) {
                heroesHealth[luckyIndex] = Math.max(heroesHealth[luckyIndex] - (bossDamage - damageToGolem), 0); // учитываю дамаг который принимает голем
            }
        }
    }

    public static void thorAbility() { // способность тора
        int thorIndex = 7;
        Random random = new Random();
        if (heroesHealth[thorIndex] > 0 && random.nextBoolean()) {
            bossStunned = true;
        }
    }

    public static void witcherAbility() { // способность ведьмака
        int witcherIndex = 6;

        if (heroesHealth[witcherIndex] > 0) {
            for (int i = 0; i < heroesHealth.length; i++) {
                if (i != witcherIndex && heroesHealth[i] <= 0) {
                    heroesHealth[i] = heroesHealth[witcherIndex];
                    System.out.println("❤ Witcher give own " + heroesHealth[witcherIndex] + " HP for " + heroesAttackType[i]);
                    heroesHealth[witcherIndex] = 0;
                }
            }
        }
    }

    public static void medicHealAbility() { //способность медика
        System.out.println("----------------------------");
        Random random = new Random();
        boolean healAbilityUsed = false;
        int medicIndex = 3;
        if (heroesHealth[medicIndex] > 0) {
            for (int i = 0; i < heroesHealth.length; i++) {
                if (i != medicIndex && heroesHealth[i] > 0 && heroesHealth[i] < 100) {
                    int randomHealAmount = random.nextInt(21) + 30; // от 30 до 50
                    heroesHealth[i] += randomHealAmount;
                    System.out.println("❤ Medic healed: " + randomHealAmount + "HP for " + heroesAttackType[i]);
                    healAbilityUsed = true;
                    break;
                }
            }
            if (!healAbilityUsed) { //если никто не нуждается
                System.out.println("No one needs healing");
            }
        } else { //если медик мертв
            System.out.println("Medic died and can't heal anybody :(");
        }
    }

    public static void heroesAttack() {
        for (int i = 0; i < heroesDamage.length; i++) {
            if (heroesHealth[i] > 0 && bossHealth > 0) {
                if (i == 7){
                    thorAbility();
                } else {
                    int damage = heroesDamage[i];
                    if (bossDefence == heroesAttackType[i]) {
                        Random random = new Random();
                        int coeff = random.nextInt(9) + 2; // 2,3,4,5,6,7,8,9,10
                        damage = heroesDamage[i] * coeff;
                        System.out.println("Critical Damage: " + damage);
                    }
                    bossHealth = Math.max(bossHealth - damage, 0);
                }
            }
        }
    }

    public static void printStatistics() {
        System.out.println("ROUND: " + roundNumber + " ------------------");
        if (bossStunned) {
            System.out.println("* Boss stunned and skip *");
        }
        System.out.println("Boss health: " + bossHealth + " damage: " + (bossStunned ? "SKIP" : bossDamage) + " " + // если босс застанен вместо урона пишется скип
                "defence: " + (bossDefence == null ? "No defence" : bossDefence));
        for (int i = 0; i < heroesHealth.length; i++) {
            if (i != 3 && i != 6) { // чтобы не отображать медика и ведьмака в пункте урона
                System.out.println(heroesAttackType[i] + " health: " + heroesHealth[i]
                        + " damage: " + heroesDamage[i]);
            } else {
                System.out.println(heroesAttackType[i] + " health: " + heroesHealth[i]);
            }
        }
    }
}
