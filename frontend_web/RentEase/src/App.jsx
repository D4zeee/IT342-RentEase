import { useState } from "react";
import { Button, Card, Typography } from "@material-tailwind/react";
import "./App.css";

function App() {
  const [count, setCount] = useState(0);

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
      <Card className="w-full max-w-md p-6 shadow-lg bg-white">
        <Typography variant="h4" color="blue-gray" className="text-center">
          Welcome to Vite + React
        </Typography>
        <Typography variant="paragraph" className="text-center mt-2 text-gray-600">
          Click the button below to increase the count.
        </Typography>
        <div className="flex justify-center mt-6">
          <Button onClick={() => setCount(count + 1)} color="blue">
            Count is {count}
          </Button>
        </div>
      </Card>
    </div>
  );
}

export default App;
