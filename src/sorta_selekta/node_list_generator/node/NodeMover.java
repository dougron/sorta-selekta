package sorta_selekta.node_list_generator.node;

public class NodeMover {
	
	private double startValue = 0;			// default value
	private double endValue;
	private int stepCount = 25;			// default value
	private int step;

	private boolean isMoving = false;
	private double cheekiness = 0.1;
	public double value;
	
	public int lineType = CHEEKY_LINE;

	public NodeMover(double start, double end, int stepCount){
		startValue = start;
		endValue = end;
		this.stepCount = stepCount;
		step = 0;
		isMoving = true;
	}
	public NodeMover(double value){
		endValue = value;
		step = 0;
		isMoving = false;;
	}
	public double next(){
		if (isMoving){
			value = getValue();
			step++;
			if (step >= stepCount){
				isMoving = false;
			}
			return value;
		} else {
			return endValue;
		}
	}
	public void moveTo(double newEnd){
		startValue = endValue;
		endValue = newEnd;
		isMoving = true;
		step = 0;
	}
	public void moveTo(double newEnd, int stepCount){
		this.stepCount = stepCount;
		moveTo(newEnd);
	}
	public String oneLineToString(){
		return "NodeMover " + startValue + ", " + endValue;
	}
// private ----------------------------------------------------------------
	private double getValue(){
		double pos = 0;
		if (lineType == STRAIGHT_LINE){
			pos = straightPathValue();
		} else if (lineType == CHEEKY_LINE){
			pos = cheekyPathValue();
		}
//		return pos;
		return (endValue - startValue) * pos + startValue;
	}
	private double straightPathValue(){
		return (double)(step) / stepCount;
	}
	private double cheekyPathValue(){
		
		double pos = (double)step / (double)stepCount;
//		System.out.println("step: " + step + " stepCount: " + stepCount + " pos: " + pos);
		double radianPos = (pos + 1.5) * Math.PI;					// 1.5 start position where 1.0 = half of cycle
		double sinPos = Math.sin(radianPos) * 0.5 + 0.5;
		double sin3pos = Math.sin(radianPos * 3) * cheekiness;		// anpother sin 3 x frequency of original
//		double sinAdd = (sinPos + sin3pos);
		double finalCheekyPos = (sinPos + sin3pos - cheekiness) / (1 - cheekiness * 2);
		return finalCheekyPos;
	}
	
	private static final int STRAIGHT_LINE = 0;
	private static final int CHEEKY_LINE = 1;
}
