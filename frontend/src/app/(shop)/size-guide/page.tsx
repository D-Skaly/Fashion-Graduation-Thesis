"use client";

import { useState } from "react";
<<<<<<< ours
import { Ruler, Shirt, Shoes, Info } from "lucide-react";
=======
import { Ruler, Shirt, Footprints, Info } from "lucide-react";
>>>>>>> theirs

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";

const topsSizes = [
  { size: "XS", us: "0-2", uk: "4-6", eu: "32-34", bust: '31-33"', waist: '24-26"', hips: '34-36"' },
  { size: "S", us: "4-6", uk: "8-10", eu: "36-38", bust: '34-36"', waist: '27-29"', hips: '37-39"' },
  { size: "M", us: "8-10", uk: "12-14", eu: "40-42", bust: '37-39"', waist: '30-32"', hips: '40-42"' },
  { size: "L", us: "12-14", uk: "16-18", eu: "44-46", bust: '40-42"', waist: '33-35"', hips: '43-45"' },
  { size: "XL", us: "16-18", uk: "20-22", eu: "48-50", bust: '43-45"', waist: '36-38"', hips: '46-48"' },
];

const bottomsSizes = [
  { size: "XS", us: "0-2", uk: "4-6", eu: "32-34", waist: '24-26"', hips: '34-36"', inseam: '30"' },
  { size: "S", us: "4-6", uk: "8-10", eu: "36-38", waist: '27-29"', hips: '37-39"', inseam: '30"' },
  { size: "M", us: "8-10", uk: "12-14", eu: "40-42", waist: '30-32"', hips: '40-42"', inseam: '32"' },
  { size: "L", us: "12-14", uk: "16-18", eu: "44-46", waist: '33-35"', hips: '43-45"', inseam: '32"' },
  { size: "XL", us: "16-18", uk: "20-22", eu: "48-50", waist: '36-38"', hips: '46-48"', inseam: '34"' },
];

const shoesSizes = [
  { size: "6", us: "6", uk: "4", eu: "37", footLength: '9.25"' },
  { size: "7", us: "7", uk: "5", eu: "38", footLength: '9.5"' },
  { size: "8", us: "8", uk: "6", eu: "39", footLength: '9.75"' },
  { size: "9", us: "9", uk: "7", eu: "40", footLength: '10.0"' },
  { size: "10", us: "10", uk: "8", eu: "41", footLength: '10.25"' },
  { size: "11", us: "11", uk: "9", eu: "42", footLength: '10.5"' },
  { size: "12", us: "12", uk: "10", eu: "43", footLength: '10.75"' },
];

function SizeTable({ title, sizes, measurements }: {
  title: string;
  sizes: Array<Record<string, string>>;
  measurements: string[];
}) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Size</TableHead>
              <TableHead>US</TableHead>
              <TableHead>UK</TableHead>
              <TableHead>EU</TableHead>
              {measurements.map((m) => (
                <TableHead key={m}>{m}</TableHead>
              ))}
            </TableRow>
          </TableHeader>
          <TableBody>
            {sizes.map((row) => (
              <TableRow key={row.size}>
                <TableCell className="font-bold">{row.size}</TableCell>
                <TableCell>{row.us}</TableCell>
                <TableCell>{row.uk}</TableCell>
                <TableCell>{row.eu}</TableCell>
                {measurements.map((m) => (
                  <TableCell key={m}>{row[m.toLowerCase()] || "-"}</TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}

export default function SizeGuidePage() {
  const [activeTab, setActiveTab] = useState("tops");

  return (
    <div className="container mx-auto px-4 py-8 max-w-5xl">
      <div className="mb-8">
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Ruler className="h-6 w-6" />
          Size Guide
        </h1>
        <p className="text-muted-foreground mt-2">
          Find your perfect fit with our size conversion charts
        </p>
      </div>

      {/* Measurement Instructions */}
      <Card className="mb-8">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Info className="h-4 w-4" />
            How to Measure
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid md:grid-cols-3 gap-6">
            <div>
              <h3 className="font-semibold mb-2">Bust</h3>
              <p className="text-sm text-muted-foreground">
                Measure around the fullest part of your bust, keeping the tape parallel to the floor.
              </p>
            </div>
            <div>
              <h3 className="font-semibold mb-2">Waist</h3>
              <p className="text-sm text-muted-foreground">
                Measure around your natural waistline, keeping the tape comfortably loose.
              </p>
            </div>
            <div>
              <h3 className="font-semibold mb-2">Hips</h3>
              <p className="text-sm text-muted-foreground">
                Measure around the fullest part of your hips, about 8" below your waist.
              </p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Size Charts */}
      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList className="grid w-full grid-cols-3">
          <TabsTrigger value="tops">
            <Shirt className="h-4 w-4 mr-2" />
            Tops
          </TabsTrigger>
          <TabsTrigger value="bottoms">Bottoms</TabsTrigger>
          <TabsTrigger value="shoes">
<<<<<<< ours
            <Shoes className="h-4 w-4 mr-2" />
=======
            <Footprints className="h-4 w-4 mr-2" />
>>>>>>> theirs
            Shoes
          </TabsTrigger>
        </TabsList>

        <TabsContent value="tops" className="mt-6">
          <SizeTable
            title="Tops & Dresses Size Chart"
            sizes={topsSizes}
            measurements={["Bust", "Waist", "Hips"]}
          />
        </TabsContent>

        <TabsContent value="bottoms" className="mt-6">
          <SizeTable
            title="Bottoms Size Chart"
            sizes={bottomsSizes}
            measurements={["Waist", "Hips", "Inseam"]}
          />
        </TabsContent>

        <TabsContent value="shoes" className="mt-6">
<<<<<<< ours
          <SizeTable
            title="Shoes Size Chart"
            sizes={shoesSizes}
            measurements={["Foot Length"]}
          />
=======
        <SizeTable
         title="Shoe Size Chart"
         sizes={shoesSizes}
             measurements={["Foot Length"]}
           />
>>>>>>> theirs
        </TabsContent>
      </Tabs>

      {/* Tips */}
      <Card className="mt-8 bg-secondary/20">
        <CardContent className="pt-6">
          <h3 className="font-semibold mb-2">Fit Tips</h3>
          <ul className="space-y-1 text-sm text-muted-foreground">
            <li>• If you're between sizes, we recommend sizing up for a more comfortable fit</li>
            <li>• Our sizes run true to standard US sizing</li>
            <li>• For tailored items, consider your body measurements rather than off-the-rack sizes</li>
            <li>• Still unsure? Contact our fit specialists for personalized recommendations</li>
          </ul>
        </CardContent>
      </Card>
    </div>
  );
}
