package simelectricity.energynet;

import simelectricity.api.node.ISEGridNode;
import simelectricity.energynet.components.GridNode;

public abstract class GridEvent extends EnergyEventBase {
    protected final ISEGridNode node1;

    private GridEvent(ISEGridNode node1) {
        this.node1 = node1;
    }

    public static class AppendNode extends GridEvent {
        public AppendNode(ISEGridNode node) {
            super(node);
        }

        @Override
        public void process(EnergyNetDataProvider dataProvider, int pass) {
        	if (pass == GADD)
        		dataProvider.addGridNode((GridNode) this.node1);
        }
        
		@Override
		public boolean changedStructure() {
			return true;
		}

		@Override
		public boolean needUpdate() {
			return true;
		}
    }

    public static class RemoveNode extends GridEvent {
        public RemoveNode(ISEGridNode node) {
            super(node);
        }

        @Override
        public void process(EnergyNetDataProvider dataProvider, int pass) {
        	if (pass == GDEL)
        		dataProvider.removeGridNode((GridNode) this.node1);
        }
        
		@Override
		public boolean changedStructure() {
			return true;
		}

		@Override
		public boolean needUpdate() {
			return true;
		}
    }

    public static class Connect extends GridEvent {
        protected final ISEGridNode node2;
        protected final double resistance;

        public Connect(ISEGridNode node1, ISEGridNode node2, double resistance) {
            super(node1);
            this.node2 = node2;
            this.resistance = resistance;
        }

        @Override
        public void process(EnergyNetDataProvider dataProvider, int pass) {
        	if (pass == GCHANGE)
        		dataProvider.addGridConnection((GridNode) this.node1, (GridNode) this.node2, this.resistance);
        }
        
		@Override
		public boolean changedStructure() {
			return true;
		}

		@Override
		public boolean needUpdate() {
			return true;
		}
    }

    public static class BreakConnection extends GridEvent {
        protected final ISEGridNode node2;

        public BreakConnection(ISEGridNode node1, ISEGridNode node2) {
            super(node1);
            this.node2 = node2;
        }

        @Override
        public void process(EnergyNetDataProvider dataProvider, int pass) {
        	if (pass == GCHANGE)
        		dataProvider.removeGridConnection((GridNode) this.node1, (GridNode) this.node2);
        }
        
		@Override
		public boolean changedStructure() {
			return true;
		}

		@Override
		public boolean needUpdate() {
			return true;
		}
    }

    public static class MakeTransformer extends GridEvent {
        protected final ISEGridNode sec;
        protected final double resistance, ratio;

        public MakeTransformer(ISEGridNode pri, ISEGridNode sec, double resistance, double ratio) {
            super(pri);
            this.sec = sec;
            this.resistance = resistance;
            this.ratio = ratio;
        }

        @Override
        public void process(EnergyNetDataProvider dataProvider, int pass) {
        	if (pass == GCHANGE)
        		dataProvider.makeTransformer((GridNode) this.node1, (GridNode) this.sec, this.ratio, this.resistance);
        }
        
		@Override
		public boolean changedStructure() {
			return false;
		}

		@Override
		public boolean needUpdate() {
			return true;
		}
    }

    public static class BreakTranformer extends GridEvent {
        public BreakTranformer(ISEGridNode node) {
            super(node);
        }

        @Override
        public void process(EnergyNetDataProvider dataProvider, int pass) {
        	if (pass == GCHANGE)
        		dataProvider.breakTransformer((GridNode) this.node1);
        }
        
		@Override
		public boolean changedStructure() {
			return false;
		}

		@Override
		public boolean needUpdate() {
			return true;
		}
    }

}
